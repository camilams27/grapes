package com.grapes.domain.model;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidade User que representa um usuário no banco de dados.
 * 
 * 📚 O QUE É UserDetails?
 * UserDetails é uma INTERFACE do Spring Security que define o "contrato"
 * de como um usuário deve ser representado para autenticação.
 * 
 * Ao implementar UserDetails, estamos dizendo ao Spring Security:
 * "Ei, minha classe User pode ser usada para autenticação!"
 * 
 * O Spring Security usa os métodos dessa interface para:
 * - Verificar se a conta está ativa (isAccountNonExpired, isEnabled, etc.)
 * - Obter as permissões/roles do usuário (getAuthorities)
 * - Obter as credenciais (getUsername, getPassword)
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @Column(nullable = false, length = 60)
    private String password;

    // ==================== CONSTRUTORES ====================

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // ==================== GETTERS E SETTERS ====================

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retorna a senha do usuário.
     * Este método é da interface UserDetails.
     */
    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // ==================== MÉTODOS DO UserDetails ====================

    /**
     * 📚 getAuthorities() - Retorna as PERMISSÕES/ROLES do usuário.
     * 
     * GrantedAuthority representa uma permissão, por exemplo:
     * - ROLE_USER (usuário comum)
     * - ROLE_ADMIN (administrador)
     * 
     * Por enquanto, todos os usuários têm a role "ROLE_USER".
     * Futuramente você pode adicionar um campo "role" no banco.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retorna uma lista com a role padrão ROLE_USER
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * 📚 getUsername() - Retorna o identificador único do usuário.
     * 
     * O Spring Security usa este método para identificar o usuário.
     * No nosso caso, usamos o EMAIL como username.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * 📚 isAccountNonExpired() - A conta expirou?
     * 
     * Retorna TRUE se a conta NÃO expirou (está válida).
     * Útil para sistemas que exigem renovação de conta periodicamente.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // A conta nunca expira (por enquanto)
    }

    /**
     * 📚 isAccountNonLocked() - A conta está bloqueada?
     * 
     * Retorna TRUE se a conta NÃO está bloqueada.
     * Útil para bloquear usuários após muitas tentativas de login erradas.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // A conta nunca é bloqueada (por enquanto)
    }

    /**
     * 📚 isCredentialsNonExpired() - As credenciais (senha) expiraram?
     * 
     * Retorna TRUE se as credenciais NÃO expiraram.
     * Útil para forçar o usuário a trocar a senha periodicamente.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // A senha nunca expira (por enquanto)
    }

    /**
     * 📚 isEnabled() - O usuário está habilitado?
     * 
     * Retorna TRUE se o usuário está ativo.
     * Útil para desativar usuários sem deletá-los do banco.
     */
    @Override
    public boolean isEnabled() {
        return true; // Usuário sempre habilitado (por enquanto)
    }
}