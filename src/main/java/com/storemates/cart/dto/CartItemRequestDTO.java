package com.storemates.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequestDTO {
    @NotNull(message = "se requiere el ID del producto")
    private Long productId;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer quantity;
}
