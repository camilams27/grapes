package com.grapes.infrastructure.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuração global de CORS.
 * 
 * 📚 POR QUE PRECISAMOS DISSO?
 * O frontend (Next.js em localhost:3000) precisa fazer requisições
 * para o backend (Spring Boot em localhost:8080).
 * Sem CORS configurado, o navegador bloqueia essas requisições.
 * 
 * 📚 COMO FUNCIONA?
 * - allowedOrigins: Quais domínios podem fazer requisições
 * - allowedMethods: Quais métodos HTTP são permitidos
 * - allowedHeaders: Quais headers podem ser enviados
 * - allowCredentials: Se cookies/auth headers são permitidos
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Origens permitidas (frontend local e produção)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://127.0.0.1:3000",
            "https://*.vercel.app"  // Para deploy no Vercel
        ));
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Headers permitidos
        configuration.setAllowedHeaders(List.of("*"));
        
        // Permite enviar cookies e Authorization header
        configuration.setAllowCredentials(true);
        
        // Quanto tempo o navegador pode cachear a resposta preflight (em segundos)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
