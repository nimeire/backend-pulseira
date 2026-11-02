package br.edu.fatecpg.backend_pulseira.dto;

import br.edu.fatecpg.backend_pulseira.model.Cuidador;

/** Responsável vinculado a uma pessoa, como a própria pessoa o enxerga. */
public record ResponsavelDTO(
        String id,
        String nome,
        String email,
        String telefone,
        boolean ativo,
        String fotoUrl) {

    public static ResponsavelDTO de(Cuidador conta) {
        return new ResponsavelDTO(
                conta.getId(),
                conta.getNome(),
                conta.getEmail(),
                conta.getTelefone(),
                Boolean.TRUE.equals(conta.getAtivo()),
                conta.getFotoUrl());
    }
}
