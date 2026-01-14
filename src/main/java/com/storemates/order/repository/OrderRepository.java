package com.storemates.order.repository;

import com.storemates.order.dto.TotalSales;
import com.storemates.order.entity.OrderEntity;
import com.storemates.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Page<OrderEntity> findAll(Pageable pageable);

    // por estado
    Page<OrderEntity> findByStatus(OrderStatus status, Pageable pageable);

    // por rango de fechas
    Page<OrderEntity> findByCreatedAtBetween(LocalDateTime start,
                                             LocalDateTime end,
                                             Pageable pageable);

    @Query("""
    SELECT new com.storemates.order.dto.TotalSales(
        COUNT(DISTINCT o.customerEmail),
        SUM(o.totalAmount)
    )
    FROM OrderEntity o""")
    TotalSales getSalesStats();

    /**
     * -borra las ordenes cada una hora
     */
    List<OrderEntity> findByStatusAndCreatedAtBefore(
            OrderStatus status,
            LocalDateTime createdAt
    );
}