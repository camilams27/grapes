package com.grapes.infrastructure.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grapes.application.dto.LoginRequest;
import com.grapes.application.dto.LoginResponse;
import com.grapes.application.dto.RegisterRequest;
import com.grapes.application.dto.RegisterResponse;
import com.grapes.application.services.AuthService;

import jakarta.validation.Valid;

/**
 * Controller de Autenticação.
 * 
 * 📚 ENDPOINTS:
 * - POST /auth/register → Cadastro de novo usuário + player
 * - POST /auth/login → Login e obtenção de token JWT
 * 
 * Segue o padrão: Controller só recebe, delega e retorna.
 * A lógica de negócio está no AuthService.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint de Registro (Cadastro).
     * 
     * POST /auth/register
     * Body: { "email": "user@email.com", "password": "123456", "nickname":
     * "Player1" }
     * 
     * 📚 O QUE ACONTECE?
     * 1. Recebe os dados do novo usuário
     * 2. Cria User (autenticação) + Player (perfil do jogo)
     * 3. Gera token JWT automaticamente
     * 4. Retorna tudo pro cliente (não precisa fazer login depois!)
     * 
     * @param request DTO com email, password e nickname
     * @return RegisterResponse com playerId, nickname, email, token e expiresIn
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            RegisterResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            // Email ou nickname já em uso
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * Endpoint de Login.
     * 
     * POST /auth/login
     * Body: { "email": "user@email.com", "password": "123456" }
     * 
     * @param request DTO com email e password
     * @return Token JWT se credenciais válidas, erro 401 se inválidas
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.authenticate(request.email(), request.password());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid credentials");
        }
    }
}

