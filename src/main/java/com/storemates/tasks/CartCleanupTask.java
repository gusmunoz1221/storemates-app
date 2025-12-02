package com.storemates.tasks;

import com.storemates.cart.repository.CartRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Component
@AllArgsConstructor
@Slf4j
public class CartCleanupTask {
    private final CartRepository cartRepository;

    /**
     * -limpia los carritos que no tuvieron actividad en las últimas 24 horas
     * -ejecutado automáticamente por el scheduler según el cron configurado
     * -se calcula un cutoff basado en la hora actual, y se eliminan todos los registros
     * -cuyo campo updatedAt sea anterior a ese momento
     */
    //@Scheduled(cron = "0 0 * * * *") // 1h
    @Scheduled(cron = "0 */5 * * * *") // 5m
    @Transactional
    public void cleanupOldCarts() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(5);

        log.info("Iniciando limpieza de carritos viejos cutoff = {}", cutoffTime);

        int deleted =  cartRepository.deleteByUpdatedAtBefore(cutoffTime);

        log.info("la cantidad de carritos que fueorn eliminados son: "+deleted);
    }
}

