package com.storemates.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubcategoryRequestDTO {
    @NotBlank(message = "El nombre de la subcategoría es obligatorio")
    private String name;
    private String description;
}
