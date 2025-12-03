package com.storemates.cart.controller;

import com.storemates.cart.dto.CartItemRequestDTO;
import com.storemates.cart.dto.CartResponseDTO;
import com.storemates.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/items/{sessionId}")
    public ResponseEntity<CartResponseDTO> addToCart(@PathVariable String sessionId,
                                                     @Valid @RequestBody CartItemRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addToCart(sessionId,
                request.getProductId(),
                request.getQuantity()));
    }

    // ELIMINAR
    @DeleteMapping("/items/{sessionId}/{productId}")
    public ResponseEntity<CartResponseDTO> removeFromCart(@PathVariable String sessionId,
                                                          @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItem(sessionId, productId));
    }

    // VACIAR CARRITO
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> clearCart(@PathVariable String sessionId) {
        cartService.clearCart(sessionId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
