package com.tuskerlanka.tourismmangement.dto;

import jakarta.validation.constraints.NotEmpty;

public record AuthenticationDto(

        @NotEmpty(message = "Username is required.")
        String username,

        @NotEmpty(message = "Password is required.")
        String password
) {
}
