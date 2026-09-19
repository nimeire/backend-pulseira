package br.edu.fatecpg.backend_pulseira.service;

import java.time.Instant;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import br.edu.fatecpg.backend_pulseira.model.PessoaMonitorada;
import br.edu.fatecpg.backend_pulseira.repository.PessoaMonitoradaRepository;

@Service
public class PessoaMonitoradaService {

    private final PessoaMonitoradaRepository pessoaRepository;

    public PessoaMonitoradaService(PessoaMonitoradaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    public PessoaMonitorada cadastrar(PessoaMonitorada pessoa) {
        if (pessoa.getNome() == null || pessoa.getNome().isBlank()) {
            throw new IllegalArgumentException("O nome da pessoa monitorada é obrigatório.");
        }
        if (pessoa.getDataNascimento() == null || pessoa.getDataNascimento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Informe uma data de nascimento válida.");
        }

        pessoa.setAtiva(true);
        pessoa.setCriadoEm(Instant.now());
        return pessoaRepository.save(pessoa);
    }

    public PessoaMonitorada buscarAtiva(String pessoaId) {
        PessoaMonitorada pessoa = pessoaRepository.findById(pessoaId)
                .orElseThrow(() -> new IllegalArgumentException("Pessoa monitorada não encontrada."));
        if (!Boolean.TRUE.equals(pessoa.getAtiva())) {
            throw new IllegalStateException("A pessoa monitorada está inativa.");
        }
        return pessoa;
    }
}
