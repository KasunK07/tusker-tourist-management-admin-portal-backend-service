package com.tuskerlanka.tourismmangement.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class User {

    @Id
    private String id;

    private String username;

    private String password;

    private String email;

    private boolean isEnabled;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime lastLogging;
}
