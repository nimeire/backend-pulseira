package br.edu.fatecpg.backend_pulseira.dto;

public record TelemetriaRequestDTO(
        String dispositivoId,
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
        Boolean quedaDetectada) {

}
