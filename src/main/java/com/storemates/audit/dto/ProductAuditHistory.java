package com.storemates.audit.dto;

import java.math.BigDecimal;

public interface ProductAuditHistory {
    String getProducto();
    BigDecimal getPrecioAnterior();
    BigDecimal getPrecioNuevo();
    Integer getStockResultante();
    String getAccion();
    String getResponsable();
    String getFechaHora();
}
