package br.edu.fatecpg.backend_pulseira.dto;

import java.time.LocalDate;
import java.util.Optional;

import br.edu.fatecpg.backend_pulseira.model.Cuidador;
import br.edu.fatecpg.backend_pulseira.model.Endereco;
import br.edu.fatecpg.backend_pulseira.model.PessoaMonitorada;

/**
 * Dados mínimos de uma pessoa monitorada para listas e para a confirmação do
 * vínculo. {@code contaAtiva} vale a conta de acesso da pessoa, se tiver uma;
 * senão, o cadastro dela.
 */
public record PessoaResumoDTO(
        String id,
        String nome,
        LocalDate dataNascimento,
        String telefone,
        String bairro,
        String cidade,
        String estado,
        boolean ativo,
        String fotoUrl) {

    public static PessoaResumoDTO de(PessoaMonitorada pessoa, Optional<Cuidador> conta) {
        Endereco endereco = pessoa.getEndereco();
        boolean ativo = conta
                .map(c -> Boolean.TRUE.equals(c.getAtivo()))
                .orElse(Boolean.TRUE.equals(pessoa.getAtiva()));
        return new PessoaResumoDTO(
                pessoa.getId(),
                pessoa.getNome(),
                pessoa.getDataNascimento(),
                pessoa.getTelefone(),
                endereco == null ? null : endereco.getBairro(),
                endereco == null ? null : endereco.getCidade(),
                endereco == null ? null : endereco.getEstado(),
                ativo,
                conta.map(Cuidador::getFotoUrl).orElse(null));
    }
}
