package com.storemates.report.service;

import com.storemates.order.entity.OrderEntity;
import com.storemates.order.entity.OrderStatus;
import com.storemates.order.repository.OrderRepository;
import com.storemates.report.exporter.SalesExcelExporter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public ByteArrayInputStream generateSalesExcel(LocalDate startDate, LocalDate endDate, OrderStatus status) throws IOException {
        SalesExcelExporter exporter = new SalesExcelExporter();

        int pageNumber = 0;
        int pageSize = 1000;
        Page<OrderEntity> page;

        do {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            // ------POR FECHA-------
            if (startDate != null && endDate != null) {
                LocalDateTime start = startDate.atStartOfDay();
                LocalDateTime end = endDate.atTime(LocalTime.MAX);
                if (status != null) {
                    // FECHA+ESTADO
                    page = orderRepository.findByCreatedAtBetweenAndStatus(start, end, status, pageable);
                } else {
                    // SOLO FECHA
                    page = orderRepository.findByCreatedAtBetween(start, end, pageable);
                }

            } else {
                // ---HISTOTIAL COMPLETO----
                if (status != null) {
                    // POR FECHA
                    page = orderRepository.findByStatus(status, pageable);
                } else {
                    // TODO
                    page = orderRepository.findAll(pageable);
                }
            }

            // Escribimos los datos de ESTA página en el Excel
            page.getContent().forEach(exporter::writeData);
            pageNumber++;

        } while (page.hasNext());
        return exporter.build();
    }
}


