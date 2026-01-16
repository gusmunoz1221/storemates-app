package com.storemates.payment.controller;

import com.storemates.order.service.OrderServiceImp;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
@Hidden
public class PaymentController {
    private final OrderServiceImp orderService;

    @PostMapping("/webhook")
    public ResponseEntity<Void> receiveWebhook(@RequestBody Map<String, Object> payload) {
        String correlationId = UUID.randomUUID().toString();

        log.info("[MP-WEBHOOK][{}] Webhook recibido", correlationId);

        orderService.processPaymentNotification(payload, correlationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/success")
    public String paymentSuccess(
            @RequestParam(value = "collection_id", required = false) String collectionId,
            @RequestParam(value = "collection_status", required = false) String collectionStatus,
            @RequestParam(value = "external_reference", required = false) String externalReference
    ) {
        return "<h1>Pago Exitoso</h1>" +
                "<p>ID de pago: " + collectionId + "</p>" +
                "<p>Estado: " + collectionStatus + "</p>" +
                "<p>Orden ID: " + externalReference + "</p>";
    }

    @GetMapping("/failure")
    public String paymentFailure(@RequestParam(value = "collection_status", required = false) String collectionStatus) {
        return "<h1>Pago Fallido</h1><p>Estado: " + collectionStatus + "</p>";
    }

    // Pendiente: Pago en efectivo (Rapipago) o tarjeta en revisión
    @GetMapping("/pending")
    public String paymentPending(@RequestParam(value = "collection_status", required = false) String collectionStatus) {
        return "<h1>Pago Pendiente</h1><p>Tu pago se está procesando...</p>";
    }

}
