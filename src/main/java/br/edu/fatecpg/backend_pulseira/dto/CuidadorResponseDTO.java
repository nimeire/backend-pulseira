package br.edu.fatecpg.backend_pulseira.dto;

import br.edu.fatecpg.backend_pulseira.model.Cuidador;
import java.time.Instant;

public record CuidadorResponseDTO(
        String id,
        String nome,
        String email,
        Boolean ativo,
        Instant criadoEm
) {
    public static CuidadorResponseDTO fromEntity(Cuidador cuidador) {
        return new CuidadorResponseDTO(
                cuidador.getId(),
                cuidador.getNome(),
                cuidador.getEmail(),
                cuidador.getAtivo(),
                cuidador.getCriadoEm()
        );
    }
}