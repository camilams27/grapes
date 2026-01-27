package com.grapes.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.grapes.domain.model.Player;

/**
 * Repositório JPA para persistência de Players.
 * Spring Data gera a implementação automaticamente.
 */
@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {

    Optional<Player> findByNickname(String nickname);

    /**
     * Busca Player pelo email do User associado.
     * Spring Data JPA resolve automaticamente: player.user.email
     */
    Optional<Player> findByUserEmail(String email);
}
