package com.storemates.order.service;


import com.storemates.order.dto.OrderRequestDTO;
import com.storemates.order.dto.OrderResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    // CHECKOUT
    OrderResponseDTO createOrder(OrderRequestDTO request);

    // BUSCAR POR ID
    OrderResponseDTO getOrderById(Long id);

    // LISTAR (ADMIN)
    Page<OrderResponseDTO> getAllOrders(Pageable pageable);
}
