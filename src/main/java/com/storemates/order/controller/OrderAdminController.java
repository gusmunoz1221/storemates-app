package com.storemates.order.controller;

import com.storemates.order.dto.OrderResponseDTO;
import com.storemates.order.dto.TotalSales;
import com.storemates.order.entity.OrderStatus;
import com.storemates.order.service.OrderService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@Hidden
public class OrderAdminController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> getAllOrders(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/status")
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersByStatus(@RequestParam OrderStatus status,
                                                                    @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(orderService.filterOrdersByStatus(status, pageable));
    }

    // Eejemlo: /ADMIN/orders/report?start=2025-11-01T00:00:00&end=2025-11-30T23:59:59
 //   @GetMapping("/report")
 //   public ResponseEntity<Page<OrderResponseDTO>> getOrdersByDateRange(
 //           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
 //           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
 //           @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
 //       return ResponseEntity.ok(orderService(start, end, pageable));
 //   }

    @GetMapping("/total-sales")
    public ResponseEntity<TotalSales> getTotalSales() {
        return ResponseEntity.ok(orderService.getTotalSales());
    }
}