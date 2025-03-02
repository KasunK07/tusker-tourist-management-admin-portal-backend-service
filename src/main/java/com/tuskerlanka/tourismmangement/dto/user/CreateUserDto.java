package com.tuskerlanka.tourismmangement.dto.user;

import com.tuskerlanka.tourismmangement.validation.password.PasswordMatch;
import jakarta.validation.constraints.NotEmpty;

@PasswordMatch
public record CreateUserDto(

        @NotEmpty(message = "Email is required.")
        String email,

        @NotEmpty(message = "Password is required.")
        String password,

        @NotEmpty(message = "Confirm password is required.")
        String confirmPassword,

        @NotEmpty(message = "phone is required.")
        String phone,

        @NotEmpty(message = "Assign a role to user.")
        String role
) {
}
