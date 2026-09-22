package br.edu.fatecpg.backend_pulseira.dto;

import br.edu.fatecpg.backend_pulseira.model.Alerta;
import br.edu.fatecpg.backend_pulseira.model.StatusAlerta;
import br.edu.fatecpg.backend_pulseira.model.TipoAlerta;

import java.time.Instant;

public record AlertaResponseDTO(
        String id,
        String pulseiraId,
        String pessoaMonitoradaId,
        String telemetriaId,
        TipoAlerta tipo,
        StatusAlerta status,
        String cuidadorReconheceuId,
        Instant geradoEm,
        Instant reconhecidoEm,
        Instant resolvidoEm,
        String observacao
) {
    public static AlertaResponseDTO fromEntity(Alerta alerta) {
        return new AlertaResponseDTO(
                alerta.getId(),
                alerta.getPulseiraId(),
                alerta.getPessoaMonitoradaId(),
                alerta.getTelemetriaId(),
                alerta.getTipo(),
                alerta.getStatus(),
                alerta.getCuidadorReconheceuId(),
                alerta.getGeradoEm(),
                alerta.getReconhecidoEm(),
                alerta.getResolvidoEm(),
                alerta.getObservacao()
        );
    }
}