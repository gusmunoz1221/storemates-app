package com.storemates.order.entity;

public enum OrderStatus{
    PENDING,    // Recién creada, pago no confirmado
    PAID,       // Pago exitoso (MercadoPago aprobado)
    SHIPPED,    // Enviado por correo
    DELIVERED,  // Entregado al cliente
    CANCELLED   // Pago rechazado o cancelado por admin
}
