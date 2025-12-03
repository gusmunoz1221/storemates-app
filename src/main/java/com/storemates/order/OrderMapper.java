package com.storemates.order;

import com.storemates.order.dto.OrderItemResponseDTO;
import com.storemates.order.dto.OrderResponseDTO;
import com.storemates.order.entity.OrderEntity;
import com.storemates.order.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderMapper {
    // De Entidad a DTO (Para mostrar la orden)
    public OrderResponseDTO entityToDto(OrderEntity entity) {
        List<OrderItemResponseDTO> itemsDto = entity.getItems().stream()
                .map(this::itemToDto)
                .toList();

        return OrderResponseDTO.builder()
                .id(entity.getId())
                .customerName(entity.getCustomerName())
                .customerEmail(entity.getCustomerEmail())
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .items(itemsDto)
                .build();
    }

    public OrderItemResponseDTO itemToDto(OrderItemEntity entity) {
        BigDecimal subtotal = entity.getPrice().multiply(BigDecimal.valueOf(entity.getQuantity()));

        return OrderItemResponseDTO.builder()
                .id(entity.getId())
                .productName(entity.getProduct().getName())
                .quantity(entity.getQuantity())
                .price(entity.getPrice())
                .subtotal(subtotal)
                .build();
    }
}
