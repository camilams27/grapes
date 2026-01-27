package com.grapes.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(unique = true, nullable = false)
    private String nickname;
    private long experience = 0;
    private int level = 1;
    private BigDecimal balance;
    private String activeSkin;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    // XP necessário para subir de nível
    private static final long totalXpToNextLevel = 100;

    public Player(String nickname, BigDecimal balance, String activeSkin) {
        this.nickname = nickname;
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

    public void setExperience(long experience) {
        this.experience = experience;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getActiveSkin() {
        return this.activeSkin;
    }

    public void setActiveSkin(String activeSkin) {
        this.activeSkin = activeSkin;
    }

}
