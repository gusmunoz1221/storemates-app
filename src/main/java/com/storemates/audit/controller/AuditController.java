package com.storemates.audit.controller;
import com.storemates.audit.dto.AuditDTO;
import com.storemates.audit.dto.ProductAuditHistory;
import com.storemates.audit.service.AuditService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/audit")
@RequiredArgsConstructor
@Hidden
public class AuditController {
    private final AuditService auditService;

    // Ejemplo de llamada :/admin/audit/products?date=16/01/2026
    @GetMapping("/products")
    public ResponseEntity<List<AuditDTO>> getProductHistory(
            @RequestParam("date") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate date
    ) {return ResponseEntity.ok(auditService.getHistoryByDate(date));}
}