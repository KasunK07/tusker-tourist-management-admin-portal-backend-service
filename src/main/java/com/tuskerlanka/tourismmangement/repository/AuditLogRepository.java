package com.tuskerlanka.tourismmangement.repository;

import com.tuskerlanka.tourismmangement.document.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
}
