package br.edu.fatecpg.backend_pulseira.service;

import java.time.Instant;
import java.util.Locale;

import org.springframework.stereotype.Service;

import br.edu.fatecpg.backend_pulseira.model.Cuidador;
import br.edu.fatecpg.backend_pulseira.repository.CuidadorRepository;

@Service
public class CuidadorService {

    private final CuidadorRepository cuidadorRepository;

    public CuidadorService(CuidadorRepository cuidadorRepository) {
        this.cuidadorRepository = cuidadorRepository;
    }

    public Cuidador cadastrar(Cuidador cuidador) {
        exigirTexto(cuidador.getNome(), "O nome do cuidador é obrigatório.");
        exigirEmail(cuidador.getEmail());
        exigirTexto(cuidador.getSenhaHash(), "A senha do cuidador é obrigatória.");

        String emailNormalizado = cuidador.getEmail().trim().toLowerCase(Locale.ROOT);
        if (cuidadorRepository.existsByEmail(emailNormalizado)) {
            throw new IllegalArgumentException("Já existe um cuidador com este e-mail.");
        }

        cuidador.setEmail(emailNormalizado);
        cuidador.setAtivo(true);
        cuidador.setCriadoEm(Instant.now());
        return cuidadorRepository.save(cuidador);
    }

    public Cuidador buscarAtivo(String cuidadorId) {
        Cuidador cuidador = cuidadorRepository.findById(cuidadorId)
                .orElseThrow(() -> new IllegalArgumentException("Cuidador não encontrado."));
        if (!Boolean.TRUE.equals(cuidador.getAtivo())) {
            throw new IllegalStateException("O cuidador está inativo.");
        }
        return cuidador;
    }

    private void exigirEmail(String email) {
        exigirTexto(email, "O e-mail é obrigatório.");
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Informe um e-mail válido.");
        }
    }

    private void exigirTexto(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
    }
}
