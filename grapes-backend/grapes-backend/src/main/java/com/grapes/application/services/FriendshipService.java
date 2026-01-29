package com.grapes.application.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grapes.domain.model.Friendship;
import com.grapes.domain.model.FriendshipStatus;
import com.grapes.domain.model.Player;
import com.grapes.infrastructure.persistence.FriendshipRepository;
import com.grapes.infrastructure.persistence.PlayerRepository;

/**
 * Service para gerenciamento de amizades.
 */
@Service
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final PlayerRepository playerRepository;

    public FriendshipService(FriendshipRepository friendshipRepository, PlayerRepository playerRepository) {
        this.friendshipRepository = friendshipRepository;
        this.playerRepository = playerRepository;
    }

    /**
     * Envia um convite de amizade
     */
    @Transactional
    public Friendship sendRequest(Player requester, String addresseeNickname) {
        // Busca o destinatário
        Player addressee = playerRepository.findByNickname(addresseeNickname)
                .orElseThrow(() -> new RuntimeException("Jogador não encontrado: " + addresseeNickname));

        // Não pode adicionar a si mesmo
        if (requester.getId().equals(addressee.getId())) {
            throw new IllegalArgumentException("Você não pode se adicionar como amigo");
        }

        // Verifica se já existe amizade ou convite
        friendshipRepository.findBetween(requester, addressee).ifPresent(f -> {
            if (f.getStatus() == FriendshipStatus.ACCEPTED) {
                throw new IllegalArgumentException("Vocês já são amigos");
            }
            if (f.getStatus() == FriendshipStatus.PENDING) {
                throw new IllegalArgumentException("Já existe um convite pendente");
            }
        });

        // Cria o convite
        Friendship friendship = new Friendship(requester, addressee);
        return friendshipRepository.save(friendship);
    }

    /**
     * Aceita um convite de amizade
     */
    @Transactional
    public Friendship acceptRequest(UUID friendshipId, Player player) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Convite não encontrado"));

        // Apenas o destinatário pode aceitar
        if (!friendship.getAddressee().getId().equals(player.getId())) {
            throw new IllegalArgumentException("Apenas o destinatário pode aceitar o convite");
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalArgumentException("Este convite já foi respondido");
        }

        friendship.accept();
        return friendshipRepository.save(friendship);
    }

    /**
     * Rejeita um convite de amizade
     */
    @Transactional
    public Friendship rejectRequest(UUID friendshipId, Player player) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Convite não encontrado"));

        // Apenas o destinatário pode rejeitar
        if (!friendship.getAddressee().getId().equals(player.getId())) {
            throw new IllegalArgumentException("Apenas o destinatário pode rejeitar o convite");
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalArgumentException("Este convite já foi respondido");
        }

        friendship.reject();
        return friendshipRepository.save(friendship);
    }

    /**
     * Remove uma amizade
     */
    @Transactional
    public void removeFriendship(UUID friendshipId, Player player) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Amizade não encontrada"));

        // Qualquer um dos dois pode remover
        if (!friendship.involves(player)) {
            throw new IllegalArgumentException("Você não faz parte dessa amizade");
        }

        friendshipRepository.delete(friendship);
    }

    /**
     * Lista amigos do player
     */
    @Transactional(readOnly = true)
    public List<Player> getFriends(Player player) {
        return friendshipRepository.findAcceptedFriendships(player).stream()
                .map(f -> f.getOtherPlayer(player))
                .collect(Collectors.toList());
    }

    /**
     * Lista convites pendentes recebidos
     */
    @Transactional(readOnly = true)
    public List<Friendship> getPendingRequests(Player player) {
        return friendshipRepository.findPendingRequestsFor(player);
    }

    /**
     * Lista convites pendentes enviados
     */
    @Transactional(readOnly = true)
    public List<Friendship> getSentRequests(Player player) {
        return friendshipRepository.findPendingRequestsFrom(player);
    }

    /**
     * Verifica se dois players são amigos
     */
    @Transactional(readOnly = true)
    public boolean areFriends(Player player1, Player player2) {
        return friendshipRepository.areFriends(player1, player2);
    }
}
