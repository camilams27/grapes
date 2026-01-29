package com.grapes.application.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO para criação de batalha
 */
public record CreateBattleRequest(
        String opponentNickname,    // Nickname do amigo (opcional se externalName)
        String externalName,        // Nome externo (opcional se opponentNickname)
        
        @NotNull(message = "Valor é obrigatório")
        @Positive(message = "Valor deve ser positivo")
        BigDecimal amount,
        
        @NotBlank(message = "Categoria é obrigatória")
        String category,
        
        String description,
        
        @NotNull(message = "Informe quem é o credor")
        Boolean iAmCreditor          // true = eu vou receber, false = eu devo
) {}
