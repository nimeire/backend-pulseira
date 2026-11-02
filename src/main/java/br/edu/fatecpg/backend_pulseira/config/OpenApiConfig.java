package br.edu.fatecpg.backend_pulseira.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Declara o HTTP Basic no Swagger. Com isso a tela ganha o botão "Authorize"
 * (e-mail e senha da conta) e as chamadas protegidas passam a funcionar por ela.
 */
@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA_BASIC = "basicAuth";

    @Bean
    OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Zelo")
                        .description("Back-end da pulseira Zelo. Use \"Authorize\" com o e-mail e a senha da conta; "
                                + "os endpoints usados pela própria pulseira (telemetria, alarme) não exigem login.")
                        .version("v1"))
                .components(new Components().addSecuritySchemes(ESQUEMA_BASIC,
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_BASIC));
    }
}
