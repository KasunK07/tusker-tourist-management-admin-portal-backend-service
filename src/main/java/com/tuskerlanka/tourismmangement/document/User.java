package com.tuskerlanka.tourismmangement.document;

import com.tuskerlanka.tourismmangement.exception.BaseException;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class User extends BaseDocument {

    @Id
    private String id;

    private String password;

    private String email;

    private String role;

    @Field(name = "is_enabled")
    private boolean isEnabled;

    @Field(name = "last_logging")
    private LocalDateTime lastLogging;
}
