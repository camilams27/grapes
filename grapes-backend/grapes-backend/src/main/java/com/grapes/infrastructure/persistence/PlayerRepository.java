package com.grapes.infrastructure.persistence;

import com.grapes.domain.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repositório JPA para persistência de Players.
 * Spring Data gera a implementação automaticamente.
 */
@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {
    // Métodos de query customizados podem ser adicionados aqui
    // Ex: Optional<Player> findByNickname(String nickname);
}
