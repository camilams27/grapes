package com.grapes.application.dto;

import java.util.UUID;

/**
 * DTO para resposta de cadastro (registro).
 * Retornado pelo endpoint POST /auth/register
 * 
 * Contém as informações básicas do usuário recém-criado
 * e o token JWT para que ele já possa usar a API imediatamente.
 */
public record RegisterResponse(
        
        /**
         * ID do Player criado
         */
        UUID playerId,
        
        /**
         * Nickname do jogador
         */
        String nickname,
        
        /**
         * Email usado para login
         */
        String email,
        
        /**
         * Token JWT para autenticação imediata
         * (o usuário não precisa fazer login após cadastro)
         */
        String token,
        
        /**
         * Tempo de expiração do token em milissegundos
         */
        Long expiresIn
) {}
