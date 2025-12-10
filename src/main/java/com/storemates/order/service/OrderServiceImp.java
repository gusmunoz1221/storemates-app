package com.storemates.order.service;

import com.storemates.cart.entity.CartEntity;
import com.storemates.cart.entity.CartItemEntity;
import com.storemates.cart.repository.CartRepository;
import com.storemates.exception.BusinessException;
import com.storemates.exception.ResourceNotFoundException;
import com.storemates.order.entity.OrderItemEntity;
import com.storemates.order.mapper.OrderMapper;
import com.storemates.order.dto.OrderRequestDTO;
import com.storemates.order.dto.OrderResponseDTO;
import com.storemates.order.entity.OrderEntity;
import com.storemates.order.repository.OrderRepository;
import com.storemates.product.entity.ProductEntity;
import com.storemates.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImp implements OrderService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;


    @Transactional
    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        CartEntity cart = cartRepository.findBySessionId(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("no se pudo encontrar el carrito o ha expirado"));

        if (cart.getItems().isEmpty())
            throw new ResourceNotFoundException("el carrito esta vacio");

        OrderEntity order = orderMapper.requestToEntity(request);

        List<OrderItemEntity> orderItems = new ArrayList<>();

        BigDecimal finalTotal = BigDecimal.ZERO;

        // convertimos CartItems en OrderItems y valido Stock
        for (CartItemEntity cartItem : cart.getItems()) {
            ProductEntity product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();

            if (product.getStock() < quantity)
                throw new BusinessException("no hay stock suficiente del producto: " + product.getName());

            product.setStock(product.getStock() - quantity);

            productRepository.save(product);

            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(product.getPrice());

            orderItems.add(orderItem);

            BigDecimal itemSubtotal = product.getPrice().multiply(BigDecimal.valueOf(quantity)); //price * qauntity
            finalTotal = finalTotal.add(itemSubtotal);

            order.setItems(orderItems);
            order.setTotalAmount(finalTotal);

            cartRepository.delete(cart);
            orderRepository.save(order);
        }

        return orderMapper.entityToDto(order);
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::entityToDto)
                .orElseThrow(() -> new ResourceNotFoundException("no se pudo encontrar el carrito o ha expirado"));
    }

    @Override
    public Page<OrderResponseDTO> getAllOrders(Pageable pageable) {
        return orderRepository
                .findAll(pageable)
                .map(orderMapper::entityToDto);
    }

    @Override
    public Page<OrderResponseDTO> filterOrdersByStatus(String status, Pageable pageable) {

        return orderRepository
                .findByStatus(status,pageable)
                .map(orderMapper::entityToDto);
    }

    @Override
    public Page<OrderResponseDTO> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return orderRepository
                .findByCreatedAtBetween(start,end,pageable)
                .map(orderMapper::entityToDto);
    }

    @Override
    public Double getTotalSales() {
        return orderRepository.getTotalSales();
    }
}
