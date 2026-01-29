package com.grapes.domain.model;

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
 * Representa uma amizade entre dois jogadores.
 * 
 * 📚 COMO FUNCIONA:
 * - requester: Quem enviou o convite
 * - addressee: Quem recebeu o convite
 * - status: PENDING (aguardando), ACCEPTED (amigos), REJECTED (recusado)
 */
@Entity
@Table(name = "friendships")
@Getter
@Setter
@NoArgsConstructor
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private Player requester;

    @ManyToOne
    @JoinColumn(name = "addressee_id", nullable = false)
    private Player addressee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendshipStatus status = FriendshipStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime acceptedAt;

    public Friendship(Player requester, Player addressee) {
        this.requester = requester;
        this.addressee = addressee;
        this.status = FriendshipStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void accept() {
        this.status = FriendshipStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();
    }

    public void reject() {
        this.status = FriendshipStatus.REJECTED;
    }

    /**
     * Verifica se o player é parte dessa amizade
     */
    public boolean involves(Player player) {
        return requester.getId().equals(player.getId()) 
            || addressee.getId().equals(player.getId());
    }

    /**
     * Retorna o outro jogador da amizade
     */
    public Player getOtherPlayer(Player me) {
        if (requester.getId().equals(me.getId())) {
            return addressee;
        }
        return requester;
    }
}
