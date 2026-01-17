package com.storemates.audit.entity;

import com.storemates.audit.AuditRevisionListener;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Entity
@Table(name = "revinfo_custom") // Nombre de la tabla de auditoría global
@RevisionEntity(AuditRevisionListener.class)
@Data
public class AuditRevisionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    private Long id;

    @RevisionTimestamp
    private long timestamp;
    private String username;
}
