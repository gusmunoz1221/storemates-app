package com.storemates.audit;

import com.storemates.audit.entity.AuditRevisionEntity;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
public class AuditRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
        AuditRevisionEntity rev = (AuditRevisionEntity) revisionEntity;

        // BUSCAMOS QUIEN ESTA LOGEADO AL MOMENTO DE AUDITAR
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // LO GUARDAMOS
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            rev.setUsername(auth.getName());
        } else {
        // SI ES UN EVENTO AUTOMATICO COMO MP, GUARDAMOS COMO "SYSTEM"
            rev.setUsername("SYSTEM");
        }

    }
}
