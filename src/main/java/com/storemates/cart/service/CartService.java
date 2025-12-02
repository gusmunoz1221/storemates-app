package com.storemates.cart.service;

import com.storemates.cart.dto.CartResponseDTO;

public interface CartService {
    // obtener el carrito actual o crear uno vacío si no existe
    CartResponseDTO getCart(String sessionId);

    // agregar un producto o sumar cantidad si ya existe
    CartResponseDTO addToCart(String sessionId, Long productId, Integer quantity);

    // eliminar un producto del carrito
    CartResponseDTO removeItem(String sessionId, Long productId);

    // vaciar el carrito
    void clearCart(String sessionId);
}
