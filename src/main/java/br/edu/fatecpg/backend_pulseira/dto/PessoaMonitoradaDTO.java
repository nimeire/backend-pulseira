package br.edu.fatecpg.backend_pulseira.dto;

import java.time.LocalDate;
import java.util.Optional;

import br.edu.fatecpg.backend_pulseira.model.Cuidador;
import br.edu.fatecpg.backend_pulseira.model.Endereco;
import br.edu.fatecpg.backend_pulseira.model.InformacoesMedicas;
import br.edu.fatecpg.backend_pulseira.model.PessoaMonitorada;

/** Cadastro completo da pessoa. O código de vínculo só aparece para a própria pessoa. */
public record PessoaMonitoradaDTO(
        String id,
        String nome,
        LocalDate dataNascimento,
        String telefone,
        Endereco endereco,
        InformacoesMedicas informacoesMedicas,
        String codigoVinculo,
        boolean ativo,
        String fotoUrl) {

    public static PessoaMonitoradaDTO de(
            PessoaMonitorada pessoa,
            boolean incluirCodigo,
            Optional<Cuidador> conta) {
        boolean ativo = conta
                .map(c -> Boolean.TRUE.equals(c.getAtivo()))
                .orElse(Boolean.TRUE.equals(pessoa.getAtiva()));
        return new PessoaMonitoradaDTO(
                pessoa.getId(),
                pessoa.getNome(),
                pessoa.getDataNascimento(),
                pessoa.getTelefone(),
                pessoa.getEndereco(),
                pessoa.getInformacoesMedicas(),
                incluirCodigo ? pessoa.getCodigoVinculo() : null,
                ativo,
                conta.map(Cuidador::getFotoUrl).orElse(null));
    }
}
