package com.storemates.order.repository;

import com.storemates.order.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Page<OrderEntity> findAll(Pageable pageable);

    // por estado
    Page<OrderEntity> findByStatus(String status, Pageable pageable);

    // por rango de fechas
    Page<OrderEntity> findByCreatedAtBetween(
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    // cantidad de clientes
    @Query("SELECT COUNT(DISTINCT o.customerEmail) FROM OrderEntity o")
    long countDistinctCustomers();

    // total de ventas
    @Query("SELECT SUM(o.total) FROM OrderEntity o")
    Double getTotalSales();
}