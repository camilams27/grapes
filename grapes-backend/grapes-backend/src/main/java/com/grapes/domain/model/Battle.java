package com.grapes.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa uma batalha (dívida) entre dois jogadores.
 * 
 * 📚 COMO FUNCIONA:
 * - creditor: Quem está para RECEBER (verde, +)
 * - debtor: Quem DEVE (vermelho, -)
 * - Pode ser entre amigos (friend != null) ou externo (externalName != null)
 * 
 * Os dois jogadores veem a batalha, mas com cores invertidas:
 * - Para o creditor: verde (+R$ 50,00)
 * - Para o debtor: vermelho (-R$ 50,00)
 */
@Entity
@Table(name = "battles")
@Getter
@Setter
@NoArgsConstructor
public class Battle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Quem criou a batalha
     */
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private Player creator;

    /**
     * O outro jogador (amigo do sistema)
     * Pode ser null se for com pessoa externa
     */
    @ManyToOne
    @JoinColumn(name = "opponent_id")
    private Player opponent;

    /**
     * Nome da pessoa externa (se não for amigo do sistema)
     * Usado quando opponent é null
     */
    private String externalName;

    /**
     * Quem é o credor (quem vai receber)?
     * true = creator é o credor
     * false = opponent/external é o credor
     */
    @Column(nullable = false)
    private boolean creatorIsCreditor;

    /**
     * Valor da batalha
     */
    @Column(nullable = false)
    private BigDecimal amount;

    /**
     * Categoria da batalha
     */
    @Column(nullable = false)
    private String category;

    /**
     * Descrição/motivo da batalha
     */
    private String description;

    /**
     * Status da batalha
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BattleStatus status = BattleStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime paidAt;

    // ========== MÉTODOS DE NEGÓCIO ==========

    /**
     * Marca a batalha como paga
     */
    public void markAsPaid() {
        this.status = BattleStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    /**
     * Verifica se o player está envolvido nessa batalha
     */
    public boolean involves(Player player) {
        if (creator.getId().equals(player.getId())) {
            return true;
        }
        if (opponent != null && opponent.getId().equals(player.getId())) {
            return true;
        }
        return false;
    }

    /**
     * Retorna se o player é o credor (quem recebe)
     */
    public boolean isCreditor(Player player) {
        boolean isCreator = creator.getId().equals(player.getId());
        return isCreator ? creatorIsCreditor : !creatorIsCreditor;
    }

    /**
     * Retorna o nome do "oponente" do ponto de vista do player
     */
    public String getOpponentName(Player player) {
        boolean isCreator = creator.getId().equals(player.getId());
        
        if (isCreator) {
            // Sou o criador, oponente é o outro
            return opponent != null ? opponent.getNickname() : externalName;
        } else {
            // Sou o oponente, "oponente" é o criador
            return creator.getNickname();
        }
    }
}
