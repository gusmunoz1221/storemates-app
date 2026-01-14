package com.storemates.cart.controller;

import com.storemates.cart.dto.CartItemRequestDTO;
import com.storemates.cart.dto.CartResponseDTO;
import com.storemates.cart.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/carts")
@AllArgsConstructor
public class CartController {
    private final CartService cartService;

    // VER CARRITO
    @GetMapping("/{sessionId}")
    public ResponseEntity<CartResponseDTO> getCart(@PathVariable String sessionId) {
        return ResponseEntity.ok(cartService.getCart(sessionId));
    }

    //  AGREGAR ITEM
    @PostMapping("/items")
    public ResponseEntity<CartResponseDTO> addToCart(
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId,
            @RequestBody CartItemRequestDTO request) {

        String resolvedSessionId = sessionId == null || sessionId.isBlank()
                ? "session-" + UUID.randomUUID().toString()
                : sessionId;

        CartResponseDTO response = cartService.addToCart(resolvedSessionId,
                request.getProductId(),
                request.getQuantity());

        // el frontend lee este header para actualizar el localStorage.
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("X-Session-Id", response.getSessionId())
                .body(response);
    }

    // ELIMINAR
    @DeleteMapping("/items/{sessionId}/{productId}")
    public ResponseEntity<CartResponseDTO> removeFromCart(@PathVariable String sessionId,
                                                          @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(sessionId, productId));
    }

    // VACIAR CARRITO
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> clearCart(@PathVariable String sessionId) {
        cartService.clearCart(sessionId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
