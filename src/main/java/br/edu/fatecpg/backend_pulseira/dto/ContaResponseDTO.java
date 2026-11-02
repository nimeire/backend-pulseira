package br.edu.fatecpg.backend_pulseira.dto;

import br.edu.fatecpg.backend_pulseira.model.Cuidador;
import br.edu.fatecpg.backend_pulseira.model.PerfilConta;

/** Dados da conta autenticada. Nunca inclui a senha nem o hash. */
public record ContaResponseDTO(
        String id,
        String nome,
        String email,
        String telefone,
        PerfilConta perfil,
        String pessoaMonitoradaId,
        String fotoUrl) {

    public static ContaResponseDTO de(Cuidador conta) {
        PerfilConta perfil = conta.getPerfil() == null ? PerfilConta.RESPONSAVEL : conta.getPerfil();
        return new ContaResponseDTO(
                conta.getId(),
                conta.getNome(),
                conta.getEmail(),
                conta.getTelefone(),
                perfil,
                conta.getPessoaMonitoradaId(),
                conta.getFotoUrl());
    }
}
