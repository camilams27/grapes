package com.grapes.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO (Record) para requisição de ganho de XP.
 * Records são imutáveis e ideais para DTOs em Java 21+.
 */
public record GainXpRequest(
        @NotNull(message = "Amount é obrigatório") @Min(value = 1, message = "Amount deve ser maior que 0") Long amount) {
}
