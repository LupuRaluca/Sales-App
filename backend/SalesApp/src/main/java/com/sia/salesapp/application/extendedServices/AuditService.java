package com.sia.salesapp.application.extendedServices;

import lombok.extern.slf4j.Slf4j;


@Slf4j

public class AuditService {

    public void logAction(String action, String entityName, Long entityId, String details) {
        log.info("[AUDIT] Action: {}, Entity: {}, ID: {}, Details: {}", action, entityName, entityId, details);
    }
}
