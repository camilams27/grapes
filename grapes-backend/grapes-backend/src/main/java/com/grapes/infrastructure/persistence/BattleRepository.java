package com.grapes.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grapes.domain.model.Battle;
import com.grapes.domain.model.Player;

@Repository
public interface BattleRepository extends JpaRepository<Battle, UUID> {

    /**
     * Busca todas as batalhas onde o player está envolvido (como criador ou oponente)
     */
    @Query("SELECT b FROM Battle b WHERE b.creator = :player OR b.opponent = :player ORDER BY b.createdAt DESC")
    List<Battle> findByPlayer(@Param("player") Player player);

    /**
     * Busca batalhas pendentes do player
     */
    @Query("SELECT b FROM Battle b WHERE (b.creator = :player OR b.opponent = :player) AND b.status = 'PENDING' ORDER BY b.createdAt DESC")
    List<Battle> findPendingByPlayer(@Param("player") Player player);

    /**
     * Busca batalhas por categoria
     */
    @Query("SELECT b FROM Battle b WHERE (b.creator = :player OR b.opponent = :player) AND b.category = :category ORDER BY b.createdAt DESC")
    List<Battle> findByPlayerAndCategory(@Param("player") Player player, @Param("category") String category);

    /**
     * Conta batalhas pendentes do player
     */
    @Query("SELECT COUNT(b) FROM Battle b WHERE (b.creator = :player OR b.opponent = :player) AND b.status = 'PENDING'")
    long countPendingByPlayer(@Param("player") Player player);
}
