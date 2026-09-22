package br.edu.fatecpg.backend_pulseira.dto;

import br.edu.fatecpg.backend_pulseira.model.Pulseira;
import br.edu.fatecpg.backend_pulseira.model.StatusPulseira;

import java.time.Instant;

public record PulseiraResponseDTO(
        String id,
        String dispositivoId,
        String pessoaMonitoradaId,
        StatusPulseira status,
        Instant vinculadaEm,
        Instant atualizadaEm
) {
    public static PulseiraResponseDTO fromEntity(Pulseira pulseira) {
        return new PulseiraResponseDTO(
                pulseira.getId(),
                pulseira.getDispositivoId(),
                pulseira.getPessoaMonitoradaId(),
                pulseira.getStatus(),
                pulseira.getVinculadaEm(),
                pulseira.getAtualizadaEm()
        );
    }
}