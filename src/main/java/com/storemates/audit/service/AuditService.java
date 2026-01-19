package com.storemates.audit.service;

import com.storemates.audit.dto.AuditDTO;
import com.storemates.audit.dto.ProductAuditHistory;
import com.storemates.audit.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditRepository auditRepository;

    public List<AuditDTO> getHistoryByDate(YearMonth yearMonth) {
        Instant start = yearMonth
                .atDay(1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();

        Instant end = yearMonth
                .atEndOfMonth()
                .atTime(LocalTime.MAX)
                .atZone(ZoneOffset.UTC)
                .toInstant();

        long startMillis = start.toEpochMilli();
        long endMillis = end.toEpochMilli();

        List<ProductAuditHistory> rawData =
                auditRepository.findAuditByDateRange(startMillis, endMillis);

        return rawData.stream()
                .map(history -> new AuditDTO(
                        history.getProducto(),
                        history.getPrecioAnterior(),
                        history.getPrecioNuevo(),
                        history.getStockResultante(),
                        history.getAccion(),
                        history.getResponsable(),
                        history.getFechaHora()))
                .toList();
    }
}