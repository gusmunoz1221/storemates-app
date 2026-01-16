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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImp implements CartService{
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;
    private final CartItemRepository cartItemRepository;

    /**
     *  -retorna el carrito asociado a la sesión
     *  -si no existe, crea un carrito vacío
     */
    @Override
    public CartResponseDTO getCart(String sessionId) {
        CartEntity cart = getOrCreateCart(sessionId);
        return cartMapper.entityToDto(cart);
    }

    /**
     *  -agrega un producto al carrito
     *  -valida que la cantidad sea mayor a cero
     *  -valida que el producto exista
     *  -re valida stock disponible
     *  -caso a: si el producto ya existe en el carrito, suma la cantidad
     *  -caso b: si no existe, crea un nuevo item
     *  -recalcula el total del carrito y persiste los cambios
     *  -lanza ResourceNotFoundException si el producto no existe
     *  -lanza BusinessException si la cantidad es inválida o no hay stock suficiente
     */
    @Override
    public CartResponseDTO addToCart(String sessionId, Long productId, Integer quantity) {
        if (quantity <= 0) throw new BusinessException("La cantidad debe ser mayor a 0");
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        if (product.getStock() < quantity) throw new BusinessException("Sin stock suficiente");

        // obtenemos (o creamos) el carrito con la lógica nueva del UUID
        CartEntity cart = getOrCreateCart(sessionId);

        // buscamos en la LISTA en memoria (Ahorramos una consulta a BD)
        Optional<CartItemEntity> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            // CASO A: Sumar cantidad
            CartItemEntity item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            if (product.getStock() < newQuantity)
                throw new BusinessException("Stock insuficiente para agregar más.");

            item.setQuantity(newQuantity);
        } else {
            // CASO B: Crear nuevo item
            CartItemEntity newItem = new CartItemEntity();
            newItem.setCart(cart); // Vinculación bidireccional importante
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(product.getPrice());

            cart.getItems().add(newItem); // Agregamos a la lista del padre
        }

        recalculateTotal(cart);

        // Al guardar el padre (Cart), se guardan los hijos (Items) automáticamente
        // si tienes CascadeType.ALL en tu entidad Cart.
        CartEntity savedCart = cartRepository.save(cart);

        return cartMapper.entityToDto(savedCart);
    }

    /**
     *  -elimina un producto del carrito
     *  -recalcula el total del carrito y persiste los cambios
     *  -lanza ResourceNotFoundException si el producto no está en el carrito
     */
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

    /**
     *  -vacía completamente el carrito
     *  -recalcula el total y persiste los cambios
     */
    @Override
    public void clearCart(String sessionId) {
        CartEntity cart = getOrCreateCart(sessionId);
        cart.getItems().clear();
        recalculateTotal(cart);
        cartRepository.save(cart);
    }

    /*----------------------METODOS PRIVADOS AUXILIARES (deberia ir en utils)----------------*/

    /**
     *  -retorna el carrito asociado a la sesión
     *  -si no existe, crea uno nuevo vacío
     */
    private CartEntity getOrCreateCart(String sessionId) {
        return cartRepository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    CartEntity cart = new CartEntity();
                    cart.setSessionId(sessionId);
                    cart.setTotalAmount(BigDecimal.ZERO);
                    return cart;
                });
    }

    /**
     *  -recalcula el total del carrito en base a los items y sus cantidades
     */
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