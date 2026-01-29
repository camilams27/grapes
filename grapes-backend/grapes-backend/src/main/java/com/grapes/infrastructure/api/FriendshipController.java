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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grapes.application.dto.FriendResponse;
import com.grapes.application.dto.FriendshipResponse;
import com.grapes.application.services.FriendshipService;
import com.grapes.application.services.PlayerService;
import com.grapes.domain.model.Player;
import com.grapes.domain.model.User;

/**
 * Controller para operações de amizade.
 * 
 * 🔒 Todas as rotas requerem autenticação JWT.
 */
@RestController
@RequestMapping("/friends")
@CrossOrigin(origins = "*")
public class FriendshipController {

    private final FriendshipService friendshipService;
    private final PlayerService playerService;

    public FriendshipController(FriendshipService friendshipService, PlayerService playerService) {
        this.friendshipService = friendshipService;
        this.playerService = playerService;
    }

    /**
     * Lista meus amigos
     * GET /friends
     */
    @GetMapping
    public ResponseEntity<?> getFriends(@AuthenticationPrincipal User user) {
        try {
            Player player = playerService.findByEmail(user.getEmail());
            List<FriendResponse> friends = friendshipService.getFriends(player).stream()
                    .map(FriendResponse::from)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(friends);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Lista convites de amizade pendentes (recebidos)
     * GET /friends/requests
     */
    @GetMapping("/requests")
    public ResponseEntity<?> getPendingRequests(@AuthenticationPrincipal User user) {
        try {
            Player player = playerService.findByEmail(user.getEmail());
            List<FriendshipResponse> requests = friendshipService.getPendingRequests(player).stream()
                    .map(FriendshipResponse::from)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(requests);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Lista convites de amizade enviados
     * GET /friends/sent
     */
    @GetMapping("/sent")
    public ResponseEntity<?> getSentRequests(@AuthenticationPrincipal User user) {
        try {
            Player player = playerService.findByEmail(user.getEmail());
            List<FriendshipResponse> requests = friendshipService.getSentRequests(player).stream()
                    .map(FriendshipResponse::from)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(requests);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Envia convite de amizade
     * POST /friends/request/{nickname}
     */
    @PostMapping("/request/{nickname}")
    public ResponseEntity<?> sendRequest(
            @AuthenticationPrincipal User user,
            @PathVariable String nickname
    ) {
        try {
            Player player = playerService.findByEmail(user.getEmail());
            var friendship = friendshipService.sendRequest(player, nickname);
            return ResponseEntity.status(HttpStatus.CREATED).body(FriendshipResponse.from(friendship));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Aceita convite de amizade
     * POST /friends/{id}/accept
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<?> acceptRequest(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ) {
        try {
            Player player = playerService.findByEmail(user.getEmail());
            var friendship = friendshipService.acceptRequest(id, player);
            return ResponseEntity.ok(FriendshipResponse.from(friendship));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Rejeita convite de amizade
     * POST /friends/{id}/reject
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ) {
        try {
            Player player = playerService.findByEmail(user.getEmail());
            var friendship = friendshipService.rejectRequest(id, player);
            return ResponseEntity.ok(FriendshipResponse.from(friendship));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Remove amizade
     * DELETE /friends/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeFriendship(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ) {
        try {
            Player player = playerService.findByEmail(user.getEmail());
            friendshipService.removeFriendship(id, player);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
