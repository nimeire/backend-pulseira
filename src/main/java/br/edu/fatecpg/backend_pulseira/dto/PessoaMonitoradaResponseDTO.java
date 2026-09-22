package br.edu.fatecpg.backend_pulseira.dto;

import br.edu.fatecpg.backend_pulseira.model.Endereco;
import br.edu.fatecpg.backend_pulseira.model.InformacoesMedicas;
import br.edu.fatecpg.backend_pulseira.model.PessoaMonitorada;

import java.time.Instant;
import java.time.LocalDate;

public record PessoaMonitoradaResponseDTO(
        String id,
        String nome,
        LocalDate dataNascimento,
        String telefone,
        Endereco endereco,
        InformacoesMedicas informacoesMedicas,
        Boolean ativa,
        Instant criadoEm
) {
    public static PessoaMonitoradaResponseDTO fromEntity(PessoaMonitorada pessoa) {
        return new PessoaMonitoradaResponseDTO(
                pessoa.getId(),
                pessoa.getNome(),
                pessoa.getDataNascimento(),
                pessoa.getTelefone(),
                pessoa.getEndereco(),
                pessoa.getInformacoesMedicas(),
                pessoa.getAtiva(),
                pessoa.getCriadoEm()
        );
    }
}