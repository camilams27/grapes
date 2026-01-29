package com.grapes.application.services;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grapes.domain.model.Player;
import com.grapes.infrastructure.persistence.PlayerRepository;

/**
 * Application Service para operações de Player.
 * 
 * 📚 RESPONSABILIDADES (após refatoração):
 * - Gerenciar operações de Player para usuários JÁ AUTENTICADOS
 * - Adicionar XP, buscar player, etc.
 * 
 * ⚠️ A CRIAÇÃO de User + Player agora é feita pelo AuthService no
 * /auth/register!
 * 
 * Isso segue o princípio de "Separação de Responsabilidades":
 * - AuthService → Autenticação (login, registro)
 * - PlayerService → Lógica de jogo (XP, level, etc.)
 */
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    /**
     * 📚 INJEÇÃO DE DEPENDÊNCIA
     * 
     * Agora só precisamos do PlayerRepository!
     * UserRepository e PasswordEncoder foram para o AuthService.
     */
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    /**
     * Adiciona XP ao Player existente.
     * Delega a lógica de level up para o método de domínio.
     *
     * @param nickname nickname do jogador
     * @param amount   quantidade de XP a adicionar
     * @return Player atualizado
     * @throws RuntimeException se o Player não for encontrado
     */
    @Transactional
    public Player addExperience(String nickname, Long amount) {
        // Busca o player pelo nickname ou lança exceção
        Player player = playerRepository.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("Player não encontrado: " + nickname));

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

    /**
     * Busca um Player pelo nickname.
     *
     * @param nickname nickname do jogador
     * @return Player encontrado
     * @throws RuntimeException se não encontrar
     */
    @Transactional(readOnly = true)
    public Player findByNickname(String nickname) {
        return playerRepository.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("Player not found: " + nickname));
    }

    /**
     * Busca um Player pelo email do User associado.
     * Usado para o endpoint /players/me (usuário logado).
     *
     * @param email email do usuário
     * @return Player encontrado
     * @throws RuntimeException se não encontrar
     */
    @Transactional(readOnly = true)
    public Player findByEmail(String email) {
        return playerRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Player not found for email: " + email));
    }

    /**
     * Busca players cujo nickname contém o termo (para autocomplete).
     *
     * @param term termo de busca
     * @return Lista de players encontrados
     */
    @Transactional(readOnly = true)
    public java.util.List<Player> searchByNickname(String term) {
        if (term == null || term.isBlank()) {
            return java.util.List.of();
        }
        return playerRepository.findByNicknameContainingIgnoreCase(term);
    }
}
