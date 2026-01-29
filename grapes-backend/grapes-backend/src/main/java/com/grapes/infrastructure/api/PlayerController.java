package com.grapes.infrastructure.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grapes.application.dto.GainXpRequest;
import com.grapes.application.dto.PlayerPrivateResponse;
import com.grapes.application.dto.PlayerPublicResponse;
import com.grapes.application.services.PlayerService;
import com.grapes.domain.model.Player;
import com.grapes.domain.model.User;

import jakarta.validation.Valid;

/**
 * REST Controller para operações de Player.
 * 
 * TODAS as rotas aqui requerem token JWT válido!
 * 
 * ENDPOINTS:
 * - GET /players/me → Dados PRIVADOS do player logado (contém email)
 * - GET /players/{id} → Dados PÚBLICOS de qualquer player (sem email)
 * - POST /players/{nickname}/xp → Adiciona XP ao player
 */
@RestController
@RequestMapping("/players")
@CrossOrigin(origins = "*")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    /**
     * Retorna os dados PRIVADOS do Player logado.
     * GET /players/me
     * 
     * � Retorna PlayerPrivateResponse (com email e id)
     * Apenas o próprio usuário pode acessar seus dados completos.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getLoggedPlayer(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not authenticated");
        }

        try {
            Player player = playerService.findByEmail(user.getEmail());
            return ResponseEntity.ok(PlayerPrivateResponse.from(player));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Retorna os dados PÚBLICOS de um Player pelo nickname.
     * GET /players/{nickname}
     * 
     * 🔓 Retorna PlayerPublicResponse (SEM email e id)
     * Qualquer usuário autenticado pode consultar.
     */
    @GetMapping("/{nickname}")
    public ResponseEntity<?> getPlayer(@PathVariable String nickname) {
        try {
            Player player = playerService.findByNickname(nickname);
            return ResponseEntity.ok(PlayerPublicResponse.from(player));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Adiciona XP a um Player existente.
     * POST /players/{nickname}/xp
     */
    @PostMapping("/{nickname}/xp")
    public ResponseEntity<?> addExperience(
            @PathVariable String nickname,
            @Valid @RequestBody GainXpRequest request) {
        try {
            Player updatedPlayer = playerService.addExperience(nickname, request.amount());
            return ResponseEntity.ok(PlayerPublicResponse.from(updatedPlayer));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Server Error: " + e.getMessage());
        }
    }

    /**
     * Busca jogadores por nickname (para autocomplete)
     * GET /players/search?q=termo
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchPlayers(@org.springframework.web.bind.annotation.RequestParam("q") String term) {
        try {
            var players = playerService.searchByNickname(term).stream()
                    .map(PlayerPublicResponse::from)
                    .toList();
            return ResponseEntity.ok(players);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching players: " + e.getMessage());
        }
    }
}
