package br.edu.fatecpg.backend_pulseira.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import br.edu.fatecpg.backend_pulseira.service.ContaService;

import lombok.RequiredArgsConstructor;

/**
 * Cria o administrador na subida do servidor, se ZELO_ADMIN_EMAIL e
 * ZELO_ADMIN_SENHA estiverem definidas e a conta ainda não existir.
 * Sem as variáveis, nenhum administrador é criado.
 */
@Component
@RequiredArgsConstructor
public class AdministradorInicial implements ApplicationRunner {

    private final ContaService contaService;

    @Value("${zelo.admin.email:}")
    private String email;

    @Value("${zelo.admin.senha:}")
    private String senha;

    @Override
    public void run(ApplicationArguments argumentos) {
        if (email.isBlank() || senha.isBlank()) {
            return;
        }
        contaService.garantirAdministrador(email, senha);
    }
}
