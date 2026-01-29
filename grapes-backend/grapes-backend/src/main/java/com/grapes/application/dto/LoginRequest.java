package com.grapes.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para requisição de login.
 * Usado no endpoint POST /auth/login
 * 
 * Login é feito com NICKNAME (mais fácil de lembrar que email)
 */
public record LoginRequest(
        @NotBlank(message = "Nickname is required")
        @Size(min = 3, max = 20, message = "Nickname must be between 3 and 20 characters")
        String nickname,

        @NotBlank(message = "Password is required")
        String password) {
}
