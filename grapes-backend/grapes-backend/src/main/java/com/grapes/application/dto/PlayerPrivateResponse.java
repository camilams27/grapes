package com.grapes.application.dto;

import java.util.UUID;

import com.grapes.domain.model.Player;

/**
 * DTO de resposta PRIVADA do Player.
 * Usado APENAS para o endpoint /players/me (próprio usuário).
 * 
 * 🔐 Contém dados sensíveis (email, id).
 * Apenas o próprio usuário pode ver esses dados.
 */
public record PlayerPrivateResponse(
        UUID id,
        String nickname,
        String email,
        int level,
        long experience,
        String activeSkin) {

    public static PlayerPrivateResponse from(Player player) {
        return new PlayerPrivateResponse(
                player.getId(),
                player.getNickname(),
                player.getUser() != null ? player.getUser().getEmail() : null,
                player.getLevel(),
                player.getExperience(),
                player.getActiveSkin());
    }
}
