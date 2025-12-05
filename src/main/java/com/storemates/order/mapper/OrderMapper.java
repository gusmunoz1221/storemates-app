package com.storemates.order.mapper;

import com.storemates.cart.entity.CartItemEntity;
import com.storemates.order.dto.OrderItemResponseDTO;
import com.storemates.order.dto.OrderRequestDTO;
import com.storemates.order.dto.OrderResponseDTO;
import com.storemates.order.entity.OrderEntity;
import com.storemates.order.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderMapper {

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

    // recibo la order padre para establecer la relación bidireccional
    public OrderItemEntity cartItemToOrderItem(CartItemEntity cartItem, OrderEntity parentOrder) {
        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setOrder(parentOrder);
        orderItem.setProduct(cartItem.getProduct());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(cartItem.getProduct().getPrice());
        return orderItem;
    }

    public OrderEntity requestToEntity(OrderRequestDTO request) {
        OrderEntity order = new OrderEntity();

        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setShippingAddress(request.getShippingAddress());
        order.setShippingCity(request.getShippingCity());
        order.setShippingZip(request.getShippingZip());
        return order;
    }
}
