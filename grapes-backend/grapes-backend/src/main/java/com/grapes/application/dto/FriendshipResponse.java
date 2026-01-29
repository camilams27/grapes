package com.grapes.application.dto;

import java.util.UUID;

import com.grapes.domain.model.Friendship;

/**
 * DTO para resposta de amizade
 */
public record FriendshipResponse(
        UUID id,
        String requesterNickname,
        String addresseeNickname,
        String status,
        String createdAt
) {
    public static FriendshipResponse from(Friendship friendship) {
        return new FriendshipResponse(
                friendship.getId(),
                friendship.getRequester().getNickname(),
                friendship.getAddressee().getNickname(),
                friendship.getStatus().name(),
                friendship.getCreatedAt().toString()
        );
    }
}
