package com.grapes.application.services;

import com.grapes.domain.model.Player;
import com.grapes.infrastructure.persistence.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Application Service para operações de Player.
 * Orquestra casos de uso, delegando lógica de negócio ao domínio.
 */
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    // Injeção de Dependência via Construtor (recomendado pelo Spring)
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    /**
     * Cria e persiste um novo Player com valores padrão.
     *
     * @param nickname nome de exibição do jogador
     * @param email    email do jogador
     * @return Player criado e persistido
     */
    @Transactional
    public Player createPlayer(String nickname, String email) {
        Player player = new Player(
                nickname,
                email,
                BigDecimal.ZERO, // Saldo inicial
                "default" // Skin padrão
        );
        return playerRepository.save(player);
    }

    /**
     * Adiciona XP ao Player existente.
     * Delega a lógica de level up para o método de domínio.
     *
     * @param playerId ID do jogador
     * @param amount   quantidade de XP a adicionar
     * @return Player atualizado
     * @throws RuntimeException se o Player não for encontrado
     */
    @Transactional
    public Player addExperience(UUID playerId, Long amount) {
        // Busca o player ou lança exceção
        Player player = this.findById(playerId);

        // Delega a lógica de negócio para o domínio (DDD)
        player.gainExperience(amount);

        // Persiste o estado atualizado
        return playerRepository.save(player);
    }

    /**
     * Busca um Player pelo ID.
     *
     * @param playerId ID do jogador
     * @return Player encontrado
     * @throws RuntimeException se não encontrar
     */
    @Transactional(readOnly = true)
    public Player findById(UUID playerId) {
        return playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player não encontrado com ID: " + playerId));
    }
}
