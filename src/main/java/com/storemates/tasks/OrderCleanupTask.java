package com.storemates.tasks;

import com.storemates.order.entity.OrderEntity;
import com.storemates.order.entity.OrderStatus;
import com.storemates.order.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class OrderCleanupTask {
    private final OrderRepository orderRepository;

    @Scheduled(cron = "0 0 * * * *") // cada hora
    @Transactional
    public void cancelExpiredOrders() {
        LocalDateTime limit = LocalDateTime.now().minusMinutes(30);

        List<OrderEntity> expiredOrders =
                orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.PENDING, limit);

        expiredOrders.forEach(order ->
                order.setStatus(OrderStatus.CANCELLED)
        );
    }
}
