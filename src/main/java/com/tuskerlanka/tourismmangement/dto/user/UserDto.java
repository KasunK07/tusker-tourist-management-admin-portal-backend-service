package com.tuskerlanka.tourismmangement.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String email;
    private String role;
    private String phone;

    private boolean isEnabled;
    private LocalDateTime lastLogging;

}
