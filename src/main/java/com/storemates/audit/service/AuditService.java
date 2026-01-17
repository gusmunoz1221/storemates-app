package com.storemates.audit.service;

import com.storemates.audit.dto.AuditDTO;
import com.storemates.audit.dto.ProductAuditHistory;
import com.storemates.audit.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditRepository auditRepository;

    public List<AuditDTO> getHistoryByDate(LocalDate date) {

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<ProductAuditHistory> rawData = auditRepository.findAuditByDateRange(start, end);

        return rawData.stream()
                .map(history -> new AuditDTO(
                        history.getProducto(),
                        history.getPrecioAnterior(),
                        history.getPrecioNuevo(),
                        history.getStockResultante(),
                        history.getAccion(),
                        history.getResponsable(),
                        history.getFechaHora()
                ))
                .collect(Collectors.toList());
    }
}