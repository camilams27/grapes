package com.grapes.application.dto;

import java.util.UUID;

import com.grapes.domain.model.Player;

/**
 * DTO para resposta de amigo (dados básicos)
 */
public record FriendResponse(
        UUID id,
        String nickname,
        int level,
        String activeSkin
) {
    public static FriendResponse from(Player player) {
        return new FriendResponse(
                player.getId(),
                player.getNickname(),
                player.getLevel(),
                player.getActiveSkin()
        );
    }
}
