package com.storemates.order.service;


import com.storemates.order.dto.OrderRequestDTO;
import com.storemates.order.dto.OrderResponseDTO;
import com.storemates.order.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface OrderService {
    // CHECKOUT
    OrderResponseDTO createOrder(OrderRequestDTO request);

    // BUSCAR POR ID
    OrderResponseDTO getOrderById(Long id);

     //  ------------(ADMIN)--------
    // LISTAR (ADMIN)
    Page<OrderResponseDTO> getAllOrders(Pageable pageable);

    // POR ESTADO
    Page<OrderResponseDTO> findByStatus(String status, Pageable pageable);

    // POR FECHAS
    Page<OrderResponseDTO> findByCreatedAtBetween(
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    // TOTAL DE VENTAS
    Double getTotalSales();
}
