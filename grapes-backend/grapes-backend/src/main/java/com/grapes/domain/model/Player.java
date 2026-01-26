package com.grapes.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Representa um jogador no sistema Grapes.
 * Contém informações de perfil, progressão (XP/Level) e saldo.
 */
@Entity
@Table(name = "players")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String nickname;
    private String email;
    private long experience = 0;
    private int level = 1;
    private BigDecimal balance;
    private String activeSkin;

    // XP necessário para subir de nível
    private static final long totalXpToNextLevel = 100;

    public Player(String nickname, String email, BigDecimal balance, String activeSkin) {
        this.nickname = nickname;
        this.email = email;
        this.balance = balance;
        this.activeSkin = activeSkin;
    }

    /**
     * Adiciona experiência ao jogador e verifica se subiu de nível.
     *
     * @param amount quantidade de XP a ser adicionada
     */
    public void gainExperience(long amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        this.experience += amount;
        this.checkLevelUp();
    }

    /**
     * Verifica se o jogador possui XP suficiente para subir de nível.
     */
    private void checkLevelUp() {
        // Passo 1: Calcula o custo ATUAL
        long costToNextLevel = calculateNextLevelCost();

        // Passo 2: O Loop verifica dinamicamente
        while (this.experience >= costToNextLevel) {
            // A ordem aqui importa:

            // 1. Paga o custo (tira do balde)
            this.experience -= costToNextLevel;

            // 2. Sobe o nível
            this.level++;

            // 3. CRUCIAL: Recalcula o custo para o PRÓXIMO loop
            costToNextLevel = calculateNextLevelCost();
        }
    }

    /**
     * Calcula o custo necessário para subir de nível. Se quiser mudar de Linear
     * para Exponencial, só mexe aqui!
     * Método auxiliar (Clean Code): A regra matemática fica isolada aqui.
     * 
     * @return custo necessário para subir de nível
     */
    private long calculateNextLevelCost() {
        return (long) this.level * this.totalXpToNextLevel;
    }

    public int getLevel() {
        return this.level;
    }

    public long getExperience() {
        return this.experience;
    }

    public long getXpToNextLevel() {
        return this.calculateNextLevelCost();
    }

}
