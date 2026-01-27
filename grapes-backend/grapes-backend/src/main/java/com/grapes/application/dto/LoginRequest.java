package com.grapes.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para requisição de login.
 * Usado no endpoint POST /auth/login
 */
public record LoginRequest(
        @NotBlank(message = "Email é obrigatório") @Email(message = "Email deve ser válido") String email,

        @NotBlank(message = "Senha é obrigatória") String password) {
}
