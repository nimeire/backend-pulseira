package br.edu.fatecpg.backend_pulseira.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Libera o fluxo mínimo de provisionamento e telemetria durante o protótipo local.
 * Antes de publicar a API, esses endpoints devem exigir autenticação.
 */
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(autorizacao -> autorizacao
                        .requestMatchers(HttpMethod.POST, "/api/telemetrias").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/pessoas-monitoradas").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/pulseiras").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/pulseiras/*/vinculo").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(httpBasic -> {})
                .build();
    }
}
