package com.grapes.infrastructure.api;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grapes.application.dto.BattleResponse;
import com.grapes.application.dto.CreateBattleRequest;
import com.grapes.application.services.BattleService;
import com.grapes.application.services.PlayerService;
import com.grapes.domain.model.Battle;
import com.grapes.domain.model.Player;
import com.grapes.domain.model.User;

import jakarta.validation.Valid;

/**
 * Controller para operações de batalhas.
 * 
 * 🔒 Todas as rotas requerem autenticação JWT.
 */
@RestController
@RequestMapping("/battles")
@CrossOrigin(origins = "*")
public class BattleController {

    private final BattleService battleService;
    private final PlayerService playerService;

    public BattleController(BattleService battleService, PlayerService playerService) {
        this.battleService = battleService;
        this.playerService = playerService;
    }

    /**
     * Lista minhas batalhas
     * GET /battles
     */
    @GetMapping
    public ResponseEntity<?> getBattles(@AuthenticationPrincipal User user) {
        try {
            Player player = playerService.findByEmail(user.getEmail());
            List<BattleResponse> battles = battleService.getPlayerBattles(player).stream()
                    .map(b -> BattleResponse.from(b, player))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(battles);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Lista batalhas pendentes
     * GET /battles/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingBattles(@AuthenticationPrincipal User user) {
        try {
            Player player = playerService.findByEmail(user.getEmail());
            List<BattleResponse> battles = battleService.getPendingBattles(player).stream()
                    .map(b -> BattleResponse.from(b, player))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(battles);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Cria uma nova batalha
     * POST /battles
     */
    @PostMapping
    public ResponseEntity<?> createBattle(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateBattleRequest request
    ) {
        try {
            Player player = playerService.findByEmail(user.getEmail());
            
            Battle battle;
            if (request.opponentNickname() != null && !request.opponentNickname().isBlank()) {
                // Batalha com amigo do sistema
                battle = battleService.createWithFriend(
                        player,
                        request.opponentNickname(),
                        request.amount(),
                        request.category(),
                        request.description(),
                        request.iAmCreditor()
                );
            } else if (request.externalName() != null && !request.externalName().isBlank()) {
                // Batalha com pessoa externa
                battle = battleService.createWithExternal(
                        player,
                        request.externalName(),
                        request.amount(),
                        request.category(),
                        request.description(),
                        request.iAmCreditor()
                );
            } else {
                return ResponseEntity.badRequest().body("Informe o nickname do amigo ou nome externo");
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(BattleResponse.from(battle, player));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Marca batalha como paga
     * POST /battles/{id}/pay
     */
    @PostMapping("/{id}/pay")
    public ResponseEntity<?> markAsPaid(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ) {
        try {
            Player player = playerService.findByEmail(user.getEmail());
            Battle battle = battleService.markAsPaid(id, player);
            return ResponseEntity.ok(BattleResponse.from(battle, player));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Remove uma batalha
     * DELETE /battles/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBattle(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ) {
        try {
            Player player = playerService.findByEmail(user.getEmail());
            battleService.delete(id, player);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
