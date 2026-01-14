package com.storemates.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryRequestDTO {
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String name;
    private String description;

}
