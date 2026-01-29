package com.grapes.infrastructure.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.grapes.domain.model.User;
import com.grapes.infrastructure.persistence.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro de Segurança JWT.
 * 
 * 📚 O QUE FAZ ESTE FILTRO?
 * Intercepta TODAS as requisições antes de chegarem aos controllers.
 * 
 * Fluxo:
 * 1. Verifica se existe um token no header "Authorization"
 * 2. Se existir, valida o token usando TokenService
 * 3. Se válido, busca o usuário no banco e autentica no Spring Security
 * 4. Se não houver token, apenas segue o fluxo (Spring Security decide depois)
 * 
 * OncePerRequestFilter garante que o filtro é executado apenas uma vez por request.
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    public SecurityFilter(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 1. Tenta recuperar o token do header Authorization
        String token = recoverToken(request);

        // 2. Se existe um token, tenta validar
        if (token != null) {
            // 3. Valida o token e obtém o email (subject)
            String email = tokenService.validateToken(token);

            // 4. Se o token é válido (email não é null)
            if (email != null) {
                // 5. Busca o usuário no banco
                User user = userRepository.findByEmail(email)
                        .orElse(null);

                // 6. Se o usuário existe, autentica no Spring Security
                if (user != null) {
                    // Cria o objeto de autenticação
                    // - Principal: o próprio usuário
                    // - Credentials: null (não precisamos da senha aqui)
                    // - Authorities: as permissões do usuário (getAuthorities do UserDetails)
                    var authentication = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

                    // Registra a autenticação no contexto do Spring Security
                    // Agora o Spring sabe quem é o usuário logado
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        // 7. Continua a cadeia de filtros (passa para o próximo filtro/controller)
        filterChain.doFilter(request, response);
    }

    /**
     * Extrai o token do header Authorization.
     * 
     * O formato esperado é: "Bearer <token>"
     * Exemplo: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     * 
     * @param request A requisição HTTP
     * @return O token JWT ou null se não existir
     */
    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        // Se não existe o header ou não começa com "Bearer ", retorna null
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        // Remove "Bearer " (7 caracteres) e retorna apenas o token
        return authHeader.substring(7);
    }
}
