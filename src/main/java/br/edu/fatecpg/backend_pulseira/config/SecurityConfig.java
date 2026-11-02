package br.edu.fatecpg.backend_pulseira.config;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import br.edu.fatecpg.backend_pulseira.repository.CuidadorRepository;
import br.edu.fatecpg.backend_pulseira.security.ContaAutenticada;

/**
 * Autenticação por HTTP Basic (e-mail + senha) contra as contas cadastradas, com
 * CORS para o site. Os endpoints usados pela própria pulseira (telemetria, alarme)
 * e o provisionamento de pessoas/pulseiras continuam abertos durante o protótipo
 * local; antes de publicar a API, eles devem exigir autenticação.
 */
@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(CuidadorRepository cuidadorRepository) {
        return username -> cuidadorRepository
                .findByEmail(username.trim().toLowerCase(Locale.ROOT))
                .map(ContaAutenticada::new)
                .orElseThrow(() -> new UsernameNotFoundException("Conta não encontrada."));
    }

    /** Origens do site autorizadas a chamar a API (ZELO_CORS_ORIGINS, separadas por vírgula). */
    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${zelo.cors.origins}") List<String> origens) {
        CorsConfiguration configuracao = new CorsConfiguration();
        configuracao.setAllowedOrigins(origens.stream().map(String::trim).toList());
        configuracao.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuracao.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuracao.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource fonte = new UrlBasedCorsConfigurationSource();
        fonte.registerCorsConfiguration("/api/**", configuracao);
        return fonte;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(autorizacao -> autorizacao
                        .requestMatchers(HttpMethod.POST, "/api/auth/cadastro").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/telemetrias").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/pessoas-monitoradas").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/pulseiras").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/pulseiras/*/vinculo").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/pulseiras/*/alarme").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                // Sem o cabeçalho WWW-Authenticate: o navegador não abre o pop-up nativo de login.
                .httpBasic(basic -> basic.authenticationEntryPoint((requisicao, resposta, excecao) -> {
                    resposta.setStatus(401);
                    resposta.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    resposta.setCharacterEncoding("UTF-8");
                    resposta.getWriter().write("{\"status\":401,\"erro\":\"E-mail ou senha incorretos.\"}");
                }))
                .build();
    }
}
