package com.grapes.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grapes.domain.model.Friendship;
import com.grapes.domain.model.Player;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, UUID> {

    /**
     * Busca todas as amizades aceitas de um player
     */
    @Query("SELECT f FROM Friendship f WHERE (f.requester = :player OR f.addressee = :player) AND f.status = 'ACCEPTED'")
    List<Friendship> findAcceptedFriendships(@Param("player") Player player);

    /**
     * Busca convites de amizade pendentes recebidos pelo player
     */
    @Query("SELECT f FROM Friendship f WHERE f.addressee = :player AND f.status = 'PENDING'")
    List<Friendship> findPendingRequestsFor(@Param("player") Player player);

    /**
     * Busca convites de amizade pendentes enviados pelo player
     */
    @Query("SELECT f FROM Friendship f WHERE f.requester = :player AND f.status = 'PENDING'")
    List<Friendship> findPendingRequestsFrom(@Param("player") Player player);

    /**
     * Verifica se já existe amizade ou convite entre dois players
     */
    @Query("SELECT f FROM Friendship f WHERE " +
           "(f.requester = :player1 AND f.addressee = :player2) OR " +
           "(f.requester = :player2 AND f.addressee = :player1)")
    Optional<Friendship> findBetween(@Param("player1") Player player1, @Param("player2") Player player2);

    /**
     * Verifica se dois players são amigos
     */
    @Query("SELECT COUNT(f) > 0 FROM Friendship f WHERE " +
           "((f.requester = :player1 AND f.addressee = :player2) OR " +
           "(f.requester = :player2 AND f.addressee = :player1)) " +
           "AND f.status = 'ACCEPTED'")
    boolean areFriends(@Param("player1") Player player1, @Param("player2") Player player2);
}
