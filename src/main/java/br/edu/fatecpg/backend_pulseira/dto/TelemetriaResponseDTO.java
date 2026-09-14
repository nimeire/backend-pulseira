package br.edu.fatecpg.backend_pulseira.dto;

import java.time.Instant;

public record TelemetriaResponseDTO(
        String id,
        String dispositivoId,
        String pulseiraId,
        String pessoaMonitoradaId,
        Instant registradaEm,
        Double latitude,
        Double longitude,
        Double altitudeMetros,
        Double precisaoMetros,
        Double aceleracaoX,
        Double aceleracaoY,
        Double aceleracaoZ,
        Double rotacaoX,
        Double rotacaoY,
        Double rotacaoZ,
        Boolean quedaDetectada
) {
}
