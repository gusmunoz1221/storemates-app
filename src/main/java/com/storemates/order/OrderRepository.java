package com.storemates.order;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
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
}