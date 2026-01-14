package com.storemates.payment.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferencePaymentMethodsRequest;
import com.mercadopago.client.preference.PreferencePaymentTypeRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.preference.Preference;
import com.storemates.order.entity.OrderEntity;
import com.storemates.order.entity.OrderItemEntity;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    @Value("${mp.access.token}")
    private String accessToken;

    @PostConstruct
    public void init() {
        if (accessToken == null || accessToken.isBlank())
            throw new IllegalStateException("MP_ACCESS_TOKEN no configurado");

        MercadoPagoConfig.setAccessToken(accessToken);
        System.out.println("--> Mercado Pago configurado correctamente con el Token.");
    }

    public String createPreference(OrderEntity order) {
        try {
            // items
            List<PreferenceItemRequest> items = new ArrayList<>();
            for (OrderItemEntity orderItem : order.getItems()) {
                PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                        .id(orderItem.getProduct().getId().toString())
                        .title(orderItem.getProduct().getName())
                        .quantity(orderItem.getQuantity())
                        .unitPrice(orderItem.getPrice().setScale(2, RoundingMode.HALF_UP))
                        .currencyId("ARS")
                        .build();
                items.add(itemRequest);
            }

            // URLs (BackUrls)
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("https://louanne-transinsular-treva.ngrok-free.dev/payments/success")
                    .pending("https://louanne-transinsular-treva.ngrok-free.dev/payments/pending")
                    .failure("https://louanne-transinsular-treva.ngrok-free.dev/payments/failure")
                    .build();

            // payer
            PreferencePayerRequest payer = PreferencePayerRequest.builder()
                    .email(order.getCustomerEmail())
                    .build();

            // request principal
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .payer(payer)
                    .externalReference(order.getId().toString())
                    .backUrls(backUrls)
                    .notificationUrl("https://louanne-transinsular-treva.ngrok-free.dev/payments/webhook")
                    //.autoReturn("approved")
                    .paymentMethods(PreferencePaymentMethodsRequest.builder()
                            .excludedPaymentTypes(List.of(
                                    PreferencePaymentTypeRequest.builder().id("ticket").build()
                            ))
                            .build())
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            return preference.getInitPoint();

        } catch (MPApiException e) {
            System.err.println("ERROR MP JSON: " + e.getApiResponse().getContent());
            throw new RuntimeException("Error MP: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error creando preferencia", e);
        }
    }
}