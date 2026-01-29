package com.grapes.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.grapes.domain.model.Battle;
import com.grapes.domain.model.Player;

/**
 * DTO para resposta de batalha (adaptado ao ponto de vista do player)
 */
public record BattleResponse(
        UUID id,
        String opponentName,        // Nome do oponente
        String opponentNickname,    // Nickname (null se externo)
        BigDecimal amount,          // Valor da batalha
        boolean isCreditor,         // EU sou o credor?
        String category,
        String description,
        String status,
        String createdAt
) {
    /**
     * Cria resposta do ponto de vista do player
     * - Se isCreditor = true: +R$ (verde, para receber)
     * - Se isCreditor = false: -R$ (vermelho, devo)
     */
    public static BattleResponse from(Battle battle, Player viewer) {
        boolean viewerIsCreator = battle.getCreator().getId().equals(viewer.getId());
        
        String opponentNickname = null;
        if (viewerIsCreator && battle.getOpponent() != null) {
            opponentNickname = battle.getOpponent().getNickname();
        } else if (!viewerIsCreator) {
            opponentNickname = battle.getCreator().getNickname();
        }
        
        return new BattleResponse(
                battle.getId(),
                battle.getOpponentName(viewer),
                opponentNickname,
                battle.getAmount(),
                battle.isCreditor(viewer),
                battle.getCategory(),
                battle.getDescription(),
                battle.getStatus().name(),
                battle.getCreatedAt().toString()
        );
    }
}
