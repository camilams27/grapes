package com.grapes.infrastructure.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.grapes.domain.model.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Service responsável pela geração e validação de tokens JWT.
 * 
 * 📚 O QUE É JWT?
 * JWT (JSON Web Token) é um padrão para transmitir informações de forma segura.
 * Estrutura: HEADER.PAYLOAD.SIGNATURE
 * 
 * - HEADER: Contém o tipo (JWT) e algoritmo de criptografia (HS256)
 * - PAYLOAD: Contém os "claims" (informações) como email, expiração, etc.
 * - SIGNATURE: Garante que o token não foi alterado (assinado com nossa chave
 * secreta)
 */
@Service
public class TokenService {

    // Lê a chave secreta do application.properties
    @Value("${jwt.secret}")
    private String secret;

    // Lê o tempo de expiração do application.properties
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Gera um token JWT para o usuário autenticado.
     * 
     * @param user O usuário que está fazendo login
     * @return String com o token JWT
     */
    public String generateToken(User user) {
        // Data atual (momento da criação do token)
        Date now = new Date();

        // Data de expiração = agora + tempo de expiração configurado
        Date expirationDate = new Date(now.getTime() + expiration);

        // Cria a chave de assinatura a partir do secret
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        // Monta e retorna o token JWT
        return Jwts.builder()
                // Subject: identificador principal do usuário (email)
                .subject(user.getEmail())

                // Issued At: quando o token foi criado
                .issuedAt(now)

                // Expiration: quando o token expira
                .expiration(expirationDate)

                // Assina o token com nossa chave secreta (algoritmo HS256)
                .signWith(key)

                // Gera a string final do token
                .compact();
    }

    /**
     * Retorna o tempo de expiração configurado.
     * Útil para informar ao cliente quanto tempo o token é válido.
     */
    public Long getExpiration() {
        return expiration;
    }

    /**
     * Valida um token JWT e retorna o email (subject) do usuário.
     * 
     * @param token O token JWT a ser validado
     * @return O email do usuário se o token for válido, null caso contrário
     */
    public String validateToken(String token) {
        try {
            // Cria a chave de assinatura a partir do secret
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

            // Parseia e valida o token
            // Se o token for inválido ou expirado, lança exceção
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            // Token inválido, expirado ou malformado
            return null;
        }
    }
}
