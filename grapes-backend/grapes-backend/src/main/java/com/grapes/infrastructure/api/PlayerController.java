package com.grapes.infrastructure.api;

import com.grapes.application.dto.CreatePlayerRequest;
import com.grapes.application.dto.GainXpRequest;
import com.grapes.application.services.PlayerService;
import com.grapes.domain.model.Player;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller (Adapter de entrada) para operações de Player.
 * Recebe requisições HTTP e delega para o Application Service.
 */
@RestController
@RequestMapping("/players")
@CrossOrigin(origins = "*")
public class PlayerController {

    private final PlayerService playerService;

    // Injeção via Construtor
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    /**
     * Cria um novo Player.
     * POST /players
     *
     * @param request DTO com nickname e email
     * @return Player criado com status 201
     */
    @PostMapping
    public ResponseEntity<Player> createPlayer(@Valid @RequestBody CreatePlayerRequest request) {
        Player player = playerService.createPlayer(request.nickname(), request.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(player);
    }

    /**
     * Adiciona XP a um Player existente.
     * POST /players/{id}/xp
     *
     * @param id      UUID do player
     * @param request DTO com amount de XP
     * @return Player atualizado
     */
    @PostMapping("/{id}/xp")
    public ResponseEntity<Player> addExperience(
            @PathVariable UUID id,
            @Valid @RequestBody GainXpRequest request) {
        Player updatedPlayer = playerService.addExperience(id, request.amount());
        return ResponseEntity.ok(updatedPlayer);
    }

    /**
     * Busca um Player pelo ID.
     * GET /players/{id}
     *
     * @param id UUID do player
     * @return Player encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable UUID id) {
        Player player = playerService.findById(id);
        return ResponseEntity.ok(player);
    }

}
