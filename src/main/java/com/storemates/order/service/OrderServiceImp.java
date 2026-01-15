package com.storemates.order.service;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import com.storemates.cart.entity.CartEntity;
import com.storemates.cart.entity.CartItemEntity;
import com.storemates.cart.repository.CartRepository;
import com.storemates.exception.BusinessException;
import com.storemates.exception.ResourceNotFoundException;
import com.storemates.notification.EmailService;
import com.storemates.order.dto.TotalSales;
import com.storemates.order.entity.OrderItemEntity;
import com.storemates.order.entity.OrderStatus;
import com.storemates.order.mapper.OrderMapper;
import com.storemates.order.dto.OrderRequestDTO;
import com.storemates.order.dto.OrderResponseDTO;
import com.storemates.order.entity.OrderEntity;
import com.storemates.order.repository.OrderRepository;
import com.storemates.payment.service.PaymentService;
import com.storemates.product.entity.ProductEntity;
import com.storemates.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
@Slf4j
public class OrderServiceImp implements OrderService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final PaymentService paymentService;
    // inyectamos para controlar la transacción manualmente
    private final TransactionTemplate transactionTemplate;
    private final EmailService emailService;

    /**
     * - Crea una orden a partir del carrito asociado a la sesión
     * - Valida que el carrito exista y no esté vacío
     * - Convierte los ítems del carrito en ítems de la orden
     * - Valida stock disponible por producto (primer filtro)
     * - Calcula el total final de la orden
     * - Persiste la orden con estado PENDING dentro de una transacción
     * - Asocia el cartId a la orden para eliminación diferida
     * - Ejecuta la creación de la preferencia de pago fuera de la transacción
     * - Retorna un DTO de la orden con la URL de checkout
     */
    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        //USAMOS TRANSACTIONTEMPLATE PARA ABRIR Y CERRAR LA TRANSACCION SOLO PARA ESTO
        OrderEntity savedOrder = transactionTemplate.execute(status -> {
            CartEntity cart = cartRepository.findBySessionId(request.getSessionId())
                    .orElseThrow(() -> new ResourceNotFoundException("No se pudo encontrar el carrito o ha expirado"));

            if (cart.getItems().isEmpty())
                throw new ResourceNotFoundException("El carrito está vacío");

            if(!isValidMail(request.getCustomerEmail()))
                throw new IllegalArgumentException("el correo no es valido!!");

            OrderEntity order = orderMapper.requestToEntity(request);
            List<OrderItemEntity> orderItems = new ArrayList<>();
            BigDecimal finalTotal = BigDecimal.ZERO;

            for (CartItemEntity cartItem : cart.getItems()) {
                ProductEntity product = cartItem.getProduct();
                int quantity = cartItem.getQuantity();

                // VALIDACION DE STOCK PRIMER FILTRO
                if (product.getStock() < quantity)
                    throw new BusinessException("No hay stock suficiente de: " + product.getName());

                OrderItemEntity orderItem = new OrderItemEntity();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(quantity);
                orderItem.setPrice(cartItem.getUnitPrice());

                orderItems.add(orderItem);
                finalTotal = finalTotal.add(orderItem.getPrice().multiply(BigDecimal.valueOf(quantity)));
            }

            order.setItems(orderItems);
            order.setTotalAmount(finalTotal);
            order.setStatus(OrderStatus.PENDING);
            order.setCartId(cart.getId());

            return orderRepository.save(order); // Retornamos la orden guardada
        });

        //          -------FUERA DE LA TRANSACCION------
        String paymentUrl = paymentService.createPreference(savedOrder);

        OrderResponseDTO response = orderMapper.entityToDto(savedOrder);
        response.setCheckoutUrl(paymentUrl);

        return response;
    }

    /**
     * - Procesa la notificación entrante del webhook de Mercado Pago
     * - Extrae el paymentId desde el payload recibido
     * - Consulta el estado real del pago a Mercado Pago
     * - Registra el estado del pago junto con el correlationId
     * - Delegada el procesamiento del resultado según el estado del pago
     * - Maneja y registra errores sin propagar excepciones al webhook
     * @param payload        cuerpo enviado por Mercado Pago
     * @param correlationId  identificador único para trazabilidad del webhook
     */
    public void processPaymentNotification(Map<String, Object> payload,String correlationId) {
        log.info(">>> INICIO WEBHOOK: [MP-WEBHOOK][{}] Recibiendo payload: ",correlationId + payload);
        try {
            Long paymentId = extractPaymentId(payload);

            PaymentClient client = new PaymentClient();
            Payment payment = client.get(paymentId);

            log.info(
                    "[MP-WEBHOOK][{}] MP status={} externalRef={}",
                    correlationId,
                    payment.getStatus(),
                    payment.getExternalReference()
            );

            Long orderId = Long.parseLong(payment.getExternalReference());
            processPaymentResult(orderId, payment.getStatus(),correlationId);

        } catch (Exception e) {
            log.error("[MP-WEBHOOK][{}] Error procesando webhook", correlationId, e);
        }
    }

    /**
     * - Procesa el resultado final del pago asociado a una orden
     * - Ejecuta la lógica dentro de una transacción controlada
     * - Valida que la orden exista y esté en estado PENDING
     * - Ignora notificaciones duplicadas o fuera de estado
     * - Actualiza la orden según el estado del pago:
     * - approved   -> procesa la orden como pagada
     * - rejected / cancelled → cancela la orden
     * @param orderId       identificador de la orden
     * @param mpStatus      estado del pago devuelto por Mercado Pago
     * @param correlationId identificador de trazabilidad del webhook
     */
    void processPaymentResult(Long orderId, String mpStatus,String correlationId) {
        //para que funcione el metodo en el repo
        transactionTemplate.executeWithoutResult(status -> {
            OrderEntity order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada ID: " + orderId));

            if (order.getStatus() != OrderStatus.PENDING) {
                log.info(
                        "[MP-WEBHOOK][{}] Orden {} ignorada (estado {})",
                        correlationId,
                        orderId,
                        order.getStatus()
                );
                return;
            }

            switch (mpStatus) {
                case "approved":
                    handleApprovedOrder(order,correlationId);
                    break;
                case "rejected":
                case "cancelled":
                    handleRejectedOrder(order);
                    break;
                default:
                    log.info("Estado MP no final: {}", mpStatus);
                    break;
            }
        });
    }


            // --- METODOS AUXILIARES PRIVADOS ---

    /**
     * - Procesa una orden con pago aprobado
     * - Descuenta stock por producto usando actualización directa en base de datos
     * - Cancela la orden si algún producto no posee stock suficiente
     * - Marca la orden como PAID si todo el stock se descuenta correctamente
     * - Elimina el carrito asociado una vez confirmado el pago
     * @param order         orden aprobada por Mercado Pago
     * @param correlationId identificador de trazabilidad del webhook
     */
    private void handleApprovedOrder(OrderEntity order,String correlationId) {
        // DESCONTAMOS STOCK USANDO QUERY EN REPO
        for (OrderItemEntity item : order.getItems()) {
            int updatedRows = productRepository.decreaseStock(item.getProduct().getId(), item.getQuantity());

            // SI NO ACLANZA EL STOCK MARCAMOS COMO ORDEN CANCELADA Y ALGUN ADMIN REEMBOLSE EL CASH
            if (updatedRows == 0) {
                log.error(
                        "[MP-WEBHOOK][{}] STOCK INSUFICIENTE | orderId={} productId={}",
                        correlationId,
                        order.getId(),
                        item.getProduct().getId()
                );
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                return;
            }
        }
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        //PARA EL ENVIO DEL MAIL PASAMOS LA ENTITY ORDER
        emailService.sendOrderConfirmation(order);
        cartRepository.deleteById(order.getCartId());
    }

    /**
     * - Extrae el paymentId desde el payload del webhook
     * - Soporta múltiples formatos enviados por Mercado Pago
     * - Prioriza el campo data.id si está presente
     * - Lanza excepción si no se puede obtener el identificador del pago
     *  param -> cuerpo enviado por Mercado Pago
     * throws BusinessException si no se puede extraer el paymentId
     */
    private void handleRejectedOrder(OrderEntity order) {
        order.setStatus(OrderStatus.ERROR);
        orderRepository.save(order);
    }

    /**
     * - Extrae el identificador del pago (paymentId) desde el payload del webhook
     * - Soporta los formatos más comunes enviados por Mercado Pago:
     *   - { data: { id: "123.." } }
     *   - { id: "123..." }
     * - Prioriza la extracción desde el nodo "data.id" si está presente
     * - Convierte el identificador a Long para su uso interno
     * -param payload cuerpo recibido en el webhook de Mercado Pago
     * -return identificador del pago
     * -throws BusinessException si no se puede extraer el paymentId del payload
     */
    @SuppressWarnings("unchecked")
    private Long extractPaymentId(Map<String, Object> payload) {
        Object data = payload.get("data");

        if (data instanceof Map && ((Map<String, Object>) data).get("id") != null)
            return Long.parseLong(((Map<String, Object>) data).get("id").toString());

        if (payload.get("id") != null)
            return Long.parseLong(payload.get("id").toString());

        throw new BusinessException("No se pudo extraer paymentId del payload");
    }

    public boolean isValidMail(String mail){
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$";
        return Pattern.matches(regex, mail);
    }


       //------------METODOS PARA ADMIN---------
    /**
     *  -retorna una orden por su ID
     *  -lanza ResourceNotFoundException si la orden no existe
     */
    @Override
    public OrderResponseDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::entityToDto)
                .orElseThrow(() -> new ResourceNotFoundException("la orden con el ID: "+id+" no existe"));
    }

    /**
     *  -retorna todas las órdenes paginadas
     */
    @Override
    public Page<OrderResponseDTO> getAllOrders(Pageable pageable) {
        return orderRepository
                .findAll(pageable)
                .map(orderMapper::entityToDto);
    }

    /**
     *  -filtra órdenes por estado
     *  -retorna el resultado paginado
     */
    @Override
    public Page<OrderResponseDTO> filterOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository
                .findByStatus(status,pageable)
                .map(orderMapper::entityToDto);
    }

    /**
     *  -retorna órdenes creadas entre dos fechas pagionado
     */
    @Override
    public Page<OrderResponseDTO> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return orderRepository
                .findByCreatedAtBetween(start,end,pageable)
                .map(orderMapper::entityToDto);
    }

    /**
     *  -retorna el total de ventas acumuladas con el total de clientes
     */
    @Override
    public TotalSales getTotalSales() {
        return orderRepository.getSalesStats();
    }
}
