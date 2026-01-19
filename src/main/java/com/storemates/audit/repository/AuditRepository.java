package com.storemates.audit.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.storemates.audit.dto.ProductAuditHistory;
import com.storemates.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties({"target", "class", "decoratedClass", "targetClass"})
public interface AuditRepository extends JpaRepository<ProductEntity,Long> {
    @Query(value = """
    SELECT
        p.name AS producto,
        LAG(p.price) OVER (PARTITION BY p.id ORDER BY r.timestamp) AS precioAnterior,
        p.price AS precioNuevo,
        p.stock AS stockResultante,
        CASE p.revtype
            WHEN 0 THEN 'CREADO'
            WHEN 1 THEN 'MODIFICADO'
            WHEN 2 THEN 'BORRADO'
        END AS accion,
        r.username AS responsable,
        TO_CHAR(
            TO_TIMESTAMP(r.timestamp / 1000),
            'DD/MM/YYYY HH24:MI:SS'
        ) AS fechaHora
    FROM products_aud p
    JOIN revinfo_custom r ON p.rev = r.id
    WHERE r.timestamp BETWEEN :startMillis AND :endMillis
    ORDER BY r.timestamp DESC
    """,
    nativeQuery = true)
    List<ProductAuditHistory> findAuditByDateRange(@Param("startMillis") long startMillis,
                                                   @Param("endMillis") long endMillis);
}
