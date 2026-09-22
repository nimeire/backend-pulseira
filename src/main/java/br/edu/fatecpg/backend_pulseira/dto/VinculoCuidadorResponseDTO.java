package br.edu.fatecpg.backend_pulseira.dto;

import br.edu.fatecpg.backend_pulseira.model.NivelAcesso;
import br.edu.fatecpg.backend_pulseira.model.VinculoCuidador;

import java.time.Instant;

public record VinculoCuidadorResponseDTO(
        String id,
        String cuidadorId,
        String pessoaMonitoradaId,
        NivelAcesso nivelAcesso,
        Boolean ativo,
        Instant criadoEm
) {
    public static VinculoCuidadorResponseDTO fromEntity(VinculoCuidador vinculo) {
        return new VinculoCuidadorResponseDTO(
                vinculo.getId(),
                vinculo.getCuidadorId(),
                vinculo.getPessoaMonitoradaId(),
                vinculo.getNivelAcesso(),
                vinculo.getAtivo(),
                vinculo.getCriadoEm()
        );
    }
}