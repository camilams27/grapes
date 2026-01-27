package com.grapes.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para requisição de cadastro (registro).
 * Usado no endpoint POST /auth/register
 * 
 * 📚 O QUE É UM record?
 * Record é uma forma concisa de criar classes imutáveis em Java 14+.
 * Ele automaticamente gera:
 * - Construtor com todos os campos
 * - Getters (email(), password(), nickname())
 * - equals(), hashCode(), toString()
 * 
 * É perfeito para DTOs porque são apenas "pacotes de dados".
 */
public record RegisterRequest(
        
        /**
         * Email do usuário (será usado para login).
         * @NotBlank = Não pode ser nulo nem vazio
         * @Email = Deve ter formato de email válido
         */
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ser válido")
        String email,

        /**
         * Senha do usuário.
         * @Size = Tamanho mínimo de 6 caracteres
         */
        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        String password,

        /**
         * Nickname do jogador (visível no jogo).
         * Será criado o Player automaticamente junto com o User.
         */
        @NotBlank(message = "Nickname é obrigatório")
        @Size(min = 3, max = 20, message = "Nickname deve ter entre 3 e 20 caracteres")
        String nickname
) {}
