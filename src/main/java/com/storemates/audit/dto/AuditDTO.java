package com.storemates.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditDTO {
    private String producto;
    private BigDecimal precioAnterior;
    private BigDecimal precioNuevo;
    private Integer stockResultante;
    private String accion;
    private String responsable;
    private String fechaHora;
}
