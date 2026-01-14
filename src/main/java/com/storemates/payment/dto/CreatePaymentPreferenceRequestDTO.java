package com.storemates.payment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CreatePaymentPreferenceRequestDTO {
    private Long orderId;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
}
