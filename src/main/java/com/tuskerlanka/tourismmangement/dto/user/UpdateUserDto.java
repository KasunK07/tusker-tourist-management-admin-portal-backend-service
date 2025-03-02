package com.tuskerlanka.tourismmangement.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {

    private String email;
    private String password;
    private String confirmPassword;
    private String role;
    private String phone;
}
