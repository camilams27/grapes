package com.grapes.application.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grapes.domain.model.Battle;
import com.grapes.domain.model.Player;
import com.grapes.infrastructure.persistence.BattleRepository;
import com.grapes.infrastructure.persistence.PlayerRepository;

/**
 * Service para gerenciamento de batalhas.
 */
@Service
public class BattleService {

    private final BattleRepository battleRepository;
    private final PlayerRepository playerRepository;

    public BattleService(BattleRepository battleRepository, PlayerRepository playerRepository) {
        this.battleRepository = battleRepository;
        this.playerRepository = playerRepository;
    }

    /**
     * Cria uma nova batalha com um amigo do sistema
     */
    @Transactional
    public Battle createWithFriend(
            Player creator,
            String opponentNickname,
            BigDecimal amount,
            String category,
            String description,
            boolean creatorIsCreditor
    ) {
        Player opponent = playerRepository.findByNickname(opponentNickname)
                .orElseThrow(() -> new RuntimeException("Jogador não encontrado: " + opponentNickname));

        if (creator.getId().equals(opponent.getId())) {
            throw new IllegalArgumentException("Você não pode criar uma batalha consigo mesmo");
        }

        Battle battle = new Battle();
        battle.setCreator(creator);
        battle.setOpponent(opponent);
        battle.setAmount(amount);
        battle.setCategory(category);
        battle.setDescription(description);
        battle.setCreatorIsCreditor(creatorIsCreditor);

        return battleRepository.save(battle);
    }

    /**
     * Cria uma nova batalha com pessoa externa (não cadastrada)
     */
    @Transactional
    public Battle createWithExternal(
            Player creator,
            String externalName,
            BigDecimal amount,
            String category,
            String description,
            boolean creatorIsCreditor
    ) {
        Battle battle = new Battle();
        battle.setCreator(creator);
        battle.setExternalName(externalName);
        battle.setAmount(amount);
        battle.setCategory(category);
        battle.setDescription(description);
        battle.setCreatorIsCreditor(creatorIsCreditor);

        return battleRepository.save(battle);
    }

    /**
     * Marca batalha como paga
     */
    @Transactional
    public Battle markAsPaid(UUID battleId, Player player) {
        Battle battle = battleRepository.findById(battleId)
                .orElseThrow(() -> new RuntimeException("Batalha não encontrada"));

        if (!battle.involves(player)) {
            throw new IllegalArgumentException("Você não faz parte dessa batalha");
        }

        battle.markAsPaid();
        return battleRepository.save(battle);
    }

    /**
     * Remove uma batalha
     */
    @Transactional
    public void delete(UUID battleId, Player player) {
        Battle battle = battleRepository.findById(battleId)
                .orElseThrow(() -> new RuntimeException("Batalha não encontrada"));

        // Apenas o criador pode remover
        if (!battle.getCreator().getId().equals(player.getId())) {
            throw new IllegalArgumentException("Apenas o criador pode remover a batalha");
        }

        battleRepository.delete(battle);
    }

    /**
     * Lista todas as batalhas do player
     */
    @Transactional(readOnly = true)
    public List<Battle> getPlayerBattles(Player player) {
        return battleRepository.findByPlayer(player);
    }

    /**
     * Lista batalhas pendentes do player
     */
    @Transactional(readOnly = true)
    public List<Battle> getPendingBattles(Player player) {
        return battleRepository.findPendingByPlayer(player);
    }

    /**
     * Busca batalha por ID
     */
    @Transactional(readOnly = true)
    public Battle findById(UUID battleId) {
        return battleRepository.findById(battleId)
                .orElseThrow(() -> new RuntimeException("Batalha não encontrada"));
    }
}
