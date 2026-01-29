package com.grapes.application.dto;

import com.grapes.domain.model.Player;

/**
 * DTO de resposta PÚBLICA do Player.
 * Usado para consultas por ID ou listagens.
 * 
 * 🔒 NÃO contém dados sensíveis (email, id).
 * Qualquer usuário autenticado pode ver esses dados.
 */
public record PlayerPublicResponse(
        String nickname,
        String activeSkin,
        int level,
        long experience) {

    public static PlayerPublicResponse from(Player player) {
        return new PlayerPublicResponse(
                player.getNickname(),
                player.getActiveSkin(),
                player.getLevel(),
                player.getExperience());
    }
}
