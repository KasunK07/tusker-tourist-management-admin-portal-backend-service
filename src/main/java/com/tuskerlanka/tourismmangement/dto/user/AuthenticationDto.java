package com.tuskerlanka.tourismmangement.dto.user;

import jakarta.validation.constraints.NotEmpty;

public record AuthenticationDto(

        @NotEmpty(message = "Username is required.")
        String email,

        @NotEmpty(message = "Password is required.")
        String password
) {
}
