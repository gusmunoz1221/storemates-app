package com.storemates.cart.service;

import com.storemates.cart.dto.CartResponseDTO;
import com.storemates.cart.entity.CartEntity;
import com.storemates.cart.entity.CartItemEntity;
import com.storemates.cart.mapper.CartMapper;
import com.storemates.cart.repository.CartItemRepository;
import com.storemates.cart.repository.CartRepository;
import com.storemates.exception.BusinessException;
import com.storemates.exception.ResourceNotFoundException;
import com.storemates.product.entity.ProductEntity;
import com.storemates.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class CartServiceImp implements CartService{
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;
    private final CartItemRepository cartItemRepository;

    @Override
    public CartResponseDTO getCart(String sessionId) {
        CartEntity cart = getOrCreateCart(sessionId);
        return cartMapper.entityToDto(cart);
    }

    @Override
    public CartResponseDTO addToCart(String sessionId, Long productId, Integer quantity) {
        if (quantity <= 0)
            throw new BusinessException("La cantidad debe ser mayor a 0");

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado ID: " + productId));

        if (product.getStock() < quantity)
            throw new BusinessException("No hay suficiente stock. Disponible: " + product.getStock());

        CartEntity cart = getOrCreateCart(sessionId);

        Optional<CartItemEntity> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        // CASO A: se supone que esta el cart -> sumamos el stock
        if (existingItem.isPresent()) {
            CartItemEntity item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            // revalido stock con la nueva cantidad total
            if (product.getStock() < newQuantity)
                throw new BusinessException("Stock insuficiente, no se puede agregar mas.");

            item.setQuantity(newQuantity);
        } else {
        // CASO B: se supone nuevo -> Creamos el item para add al cart
            CartItemEntity newItem = new CartItemEntity();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(product.getPrice());

            cart.getItems().add(newItem);
        }

        // se recalcula el TOTAL del carrito y guardamos
        recalculateTotal(cart);
        CartEntity savedCart = cartRepository.save(cart);

        return cartMapper.entityToDto(savedCart);
    }

    @Override
    public CartResponseDTO removeItemFromCart(String sessionId, Long productId) {
        CartEntity cart = getOrCreateCart(sessionId);

        // si cumple la condicion lo elimina de la bd y devuelve true
        boolean removed = cart.getItems()
                .removeIf(item -> item.getProduct().getId().equals(productId));

        if (!removed)
            throw new ResourceNotFoundException("El producto no esta en el carrito");

        recalculateTotal(cart);
        CartEntity savedCart = cartRepository.save(cart);

        return cartMapper.entityToDto(savedCart);
    }

    @Override
    public void clearCart(String sessionId) {
        CartEntity cart = getOrCreateCart(sessionId);
        cart.getItems().clear();
        recalculateTotal(cart);
        cartRepository.save(cart);
    }

    /*----------------------METODOS PRIVADOS AUXILIARES (deberia ir en utils)----------------*/

    private CartEntity getOrCreateCart(String sessionId) {
        return cartRepository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    // si no existe creamos uno nuevo vacío
                    CartEntity newCart = new CartEntity();
                    newCart.setSessionId(sessionId);
                    newCart.setTotalAmount(BigDecimal.ZERO);
                    return cartRepository.save(newCart);
                });
    }

    private void recalculateTotal(CartEntity cart) {
        BigDecimal total = cart.getItems()
                .stream()
                .map(item -> {
                    BigDecimal unitPrice = item.getUnitPrice() != null
                            ? item.getUnitPrice()
                            : BigDecimal.ZERO;

                    BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());

                    return unitPrice.multiply(quantity);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(total);
    }
}