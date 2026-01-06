package com.storemates.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDTO {
    @NotBlank(message = " se requiere Email")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = " se requiere Contraseña")
    private String password;
}
