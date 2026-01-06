package com.storemates.auth.dto;

import lombok.AllArgsConstructor;import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {
    String token;
}
