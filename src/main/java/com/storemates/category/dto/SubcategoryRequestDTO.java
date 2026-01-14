package com.storemates.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubcategoryRequestDTO {
    @NotBlank(message = "El nombre de la subcategoría es obligatorio")
    private String name;
    private String description;
}
