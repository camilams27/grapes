package com.grapes.application.services;

import java.math.BigDecimal;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.grapes.application.dto.LoginResponse;
import com.grapes.application.dto.RegisterRequest;
import com.grapes.application.dto.RegisterResponse;
import com.grapes.domain.model.Player;
import com.grapes.domain.model.User;
import com.grapes.infrastructure.persistence.PlayerRepository;
import com.grapes.infrastructure.persistence.UserRepository;
import com.grapes.infrastructure.security.TokenService;

/**
 * Application Service para operações de Autenticação.
 * 
 * 📚 RESPONSABILIDADES:
 * - Login: Valida credenciais e retorna token JWT
 * - Registro: Cria novo usuário + player e retorna token JWT
 * 
 * Segue o princípio de "Single Responsibility":
 * Tudo relacionado a AUTENTICAÇÃO fica aqui.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    /**
     * 📚 INJEÇÃO DE DEPENDÊNCIA VIA CONSTRUTOR
     * 
     * O Spring vê que AuthService precisa dessas 4 dependências
     * e automaticamente injeta as instâncias gerenciadas por ele.
     * 
     * Isso é possível porque:
     * - UserRepository tem @Repository
     * - PlayerRepository tem @Repository
     * - PasswordEncoder é um @Bean no SecurityConfig
     * - TokenService tem @Service
     */
    public AuthService(
            UserRepository userRepository,
            PlayerRepository playerRepository,
                    PasswordEncoder passwordEncoder,
            TokenService tokenService) {
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    /**
     * Autentica o usuário pelo NICKNAME e retorna um token JWT.
     *
     * @param nickname nickname do jogador
     * @param password senha em texto puro
     * @return LoginResponse com o token JWT
     * @throws RuntimeException se as credenciais forem inválidas
     */
    public LoginResponse authenticate(String nickname, String password) {
        // Passo 1: Buscar PLAYER pelo nickname
        Player player = playerRepository.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // Passo 2: Obter o User associado ao Player
        User user = player.getUser();
        if (user == null) {
            throw new RuntimeException("Invalid credentials");
        }

        // Passo 3: Verificar se a senha confere
        boolean passwordMatches = passwordEncoder.matches(password, user.getPassword());

        if (!passwordMatches) {
            throw new RuntimeException("Invalid credentials");
        }

        // Passo 4: Gerar o Token JWT
        String token = tokenService.generateToken(user);

        // Passo 5: Retornar a resposta
        return new LoginResponse(token, tokenService.getExpiration());
    }

    /**
     * Registra um novo usuário e cria seu Player associado.
     * 
     * 📚 @Transactional
     * Garante que User e Player são criados JUNTOS.
     * Se der erro em qualquer ponto, AMBOS são desfeitos (rollback).
     * 
     * Isso evita situações como:
     * - Ter um User sem Player (órfão)
     * - Ter um Player sem User
     *
     * @param request DTO com email, password e nickname
     * @return RegisterResponse com dados do player + token JWT
     * @throws IllegalArgumentException se email ou nickname já existirem
     */
    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        // ========== VALIDAÇÕES ==========

        // Verifica se email já está em uso
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email já está em uso");
        }

        // Verifica se nickname já está em uso
        if (playerRepository.findByNickname(request.nickname()).isPresent()) {
            throw new IllegalArgumentException("Nickname já está em uso");
        }

        // ========== CRIAÇÃO DO USER ==========

        // Faz o hash da senha usando BCrypt
        // NUNCA salve senha em texto puro!
        String hashedPassword = passwordEncoder.encode(request.password());

        // Cria e salva o User
        User user = new User(request.email(), hashedPassword);
        user = userRepository.save(user);

        // ========== CRIAÇÃO DO PLAYER ==========

        // Cria o Player com valores iniciais padrão
        Player player = new Player();
        player.setNickname(request.nickname());
        player.setBalance(BigDecimal.ZERO); // Começa sem dinheiro
        player.setActiveSkin("default"); // Skin padrão
        player.setUser(user); // Associa ao User criado

        player = playerRepository.save(player);

        // ========== GERA TOKEN JWT ==========

        // O usuário já recebe o token, não precisa fazer login após cadastro!
        String token = tokenService.generateToken(user);

        // ========== RETORNA A RESPOSTA ==========

        return new RegisterResponse(
                player.getId(),
                player.getNickname(),
                user.getEmail(),
                token,
                tokenService.getExpiration());
    }
}
