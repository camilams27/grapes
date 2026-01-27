package com.grapes.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.grapes.infrastructure.security.SecurityFilter;

/**
 * Configuração de Segurança do Spring Security.
 * 
 * 📚 COMO FUNCIONA A CADEIA DE FILTROS:
 * Request → SecurityFilter (nosso) → UsernamePasswordAuthenticationFilter → ...
 * → Controller
 * 
 * O SecurityFilter valida o JWT ANTES dos filtros padrão do Spring.
 * Se o token for válido, autentica o usuário no SecurityContext.
 * O Spring Security então verifica se o usuário tem permissão para acessar a
 * rota.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final SecurityFilter securityFilter;

        public SecurityConfig(SecurityFilter securityFilter) {
                this.securityFilter = securityFilter;
        }

        /**
         * Bean do PasswordEncoder usando BCrypt.
         * BCrypt é o algoritmo recomendado para hash de senhas:
         * - Inclui salt automático
         * - É adaptável (pode ajustar o custo computacional)
         * - Resistente a ataques de rainbow table
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        /**
         * Configuração do filtro de segurança.
         * 
         * 🔓 Rotas Públicas (PERMIT ALL):
         * - POST /auth/login → Login para obter token
         * - POST /players → Cadastro de novos jogadores
         * 
         * 🔒 Rotas Protegidas (AUTHENTICATED):
         * - Todas as outras rotas requerem token JWT válido
         */
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                // Desabilita CSRF (necessário para APIs REST stateless)
                                .csrf(csrf -> csrf.disable())

                                // Configura as permissões de acesso às rotas
                                .authorizeHttpRequests(auth -> auth
                                                // Rotas públicas (não precisam de autenticação)
                                                // Login e cadastro de usuário são públicos
                                                .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()

                                                // H2 Console (apenas para desenvolvimento)
                                                .requestMatchers("/h2-console/**").permitAll()

                                                // Todas as outras rotas precisam de autenticação
                                                .anyRequest().authenticated())

                                // Configura sessão como STATELESS (APIs REST não devem manter sessão)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                // Desabilita frames para permitir H2 Console funcionar
                                .headers(headers -> headers
                                                .frameOptions(frame -> frame.disable()))

                                // Desabilita o formulário de login padrão
                                .formLogin(form -> form.disable())

                                // Desabilita HTTP Basic
                                .httpBasic(basic -> basic.disable())

                                // Configura tratamento de exceções de autenticação
                                // Retorna 401 Unauthorized com JSON ao invés do padrão 403 Forbidden
                                .exceptionHandling(ex -> ex
                                                .authenticationEntryPoint((request, response, authException) -> {
                                                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                                                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                                                        response.getWriter().write(
                                                                        "{\"error\": \"Unauthorized\", \"message\": \"Authentication required\"}");
                                                }))

                                // Adiciona nosso filtro JWT ANTES do filtro de autenticação padrão
                                // Isso garante que o token seja validado antes de qualquer outra verificação
                                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
