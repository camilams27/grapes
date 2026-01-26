package com.grapes.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO (Record) para criação de um novo Player.
 */
public record CreatePlayerRequest(
        @NotBlank(message = "Nickname é obrigatório") @Size(min = 3, max = 20, message = "Nickname deve ter entre 3 e 20 caracteres") String nickname,

        @NotBlank(message = "Email é obrigatório") @Email(message = "Email deve ser válido") String email) {
}
