package com.tuskerlanka.tourismmangement.document;

import com.tuskerlanka.tourismmangement.enums.AuditLogAction;
import com.tuskerlanka.tourismmangement.enums.AuditLogStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "audit_log")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AuditLog {
    @Id
    private String id;

    private String method;

    private String endPoint;

    private String username;

    private AuditLogAction action;

    private AuditLogStatus status;

    private LocalDateTime loggedAt;
}
