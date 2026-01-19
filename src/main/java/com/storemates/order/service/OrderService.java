package com.storemates.order.service;


import com.storemates.order.dto.OrderRequestDTO;
import com.storemates.order.dto.OrderResponseDTO;
import com.storemates.order.dto.TotalSales;
import com.storemates.order.entity.OrderEntity;
import com.storemates.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface OrderService {
    // CHECKOUT
    OrderResponseDTO createOrder(OrderRequestDTO request);

    // BUSCAR ORDEN POR ID
    OrderResponseDTO getOrderById(Long id);

     //  ------------(ADMIN)--------
    // LISTAR TODAS LAS ORDENES (ADMIN)
    Page<OrderResponseDTO> getAllOrders(Pageable pageable);

    // BUSCAR ORDEN POR ESTADO
    Page<OrderResponseDTO> filterOrdersByStatus(OrderStatus status, Pageable pageable);

    // BUSCAR ORDENB POR FECHAS
   // Page<OrderResponseDTO> findByCreatedAtBetween(LocalDateTime start,
    //                                              LocalDateTime end,
     //                                             Pageable pageable);

    // TOTAL DE VENTAS
    TotalSales getTotalSales();
}
