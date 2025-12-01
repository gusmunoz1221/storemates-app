package com.storemates.cart.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartResponseDTO {
    private Long id;
    private String sessionId;
    private BigDecimal totalAmount; // Suma de todos los subtotales
    private int totalItems;
    private List<CartItemResponseDTO> items;
}
