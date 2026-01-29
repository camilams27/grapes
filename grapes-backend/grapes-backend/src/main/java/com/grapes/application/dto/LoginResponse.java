package com.grapes.application.dto;

/**
 * DTO para resposta de login bem-sucedido.
 * Contém o token JWT que o cliente deve usar nas próximas requisições.
 */
public record LoginResponse(
        String token,
        String type, // Tipo do token (Bearer)
        Long expiresIn // Tempo de expiração em milissegundos
) {
    /**
     * Construtor conveniente que já define o tipo como "Bearer"
     */
    public LoginResponse(String token, Long expiresIn) {
        this(token, "Bearer", expiresIn);
    }
}
