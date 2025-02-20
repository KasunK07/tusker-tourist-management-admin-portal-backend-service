package com.tuskerlanka.tourismmangement.service.impl;

import com.tuskerlanka.tourismmangement.document.AuditLog;
import com.tuskerlanka.tourismmangement.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl {

    private final static Logger logger = LoggerFactory.getLogger(AuditLogServiceImpl.class);

    private final AuditLogRepository auditLogRepository;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(AuditLog auditLog) {
        try {
            this.auditLogRepository.save(auditLog);
            logger.info("log saved successfully in the db");
        } catch (Exception ex) {
            logger.error("unknown error occur when saving audit log: {}", ex.getMessage());
        }
    }
}
