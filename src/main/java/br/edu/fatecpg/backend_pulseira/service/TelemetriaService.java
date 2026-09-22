package br.edu.fatecpg.backend_pulseira.service;

import br.edu.fatecpg.backend_pulseira.dto.TelemetriaRequestDTO;
import br.edu.fatecpg.backend_pulseira.dto.TelemetriaResponseDTO;
import br.edu.fatecpg.backend_pulseira.model.Alerta;
import br.edu.fatecpg.backend_pulseira.model.StatusAlerta;
import br.edu.fatecpg.backend_pulseira.model.Telemetria;
import br.edu.fatecpg.backend_pulseira.model.TipoAlerta;
import br.edu.fatecpg.backend_pulseira.repository.AlertaRepository;
import br.edu.fatecpg.backend_pulseira.repository.PulseiraRepository;
import br.edu.fatecpg.backend_pulseira.repository.TelemetriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TelemetriaService {

    private final TelemetriaRepository telemetriaRepository;
    private final AlertaRepository alertaRepository;

    public TelemetriaResponseDTO salvar (TelemetriaRequestDTO dto) {
        Telemetria telemetria = new Telemetria();

        telemetria.setDispositivoId(dto.dispositivoId());
        telemetria.setLatitude(dto.latitude());
        telemetria.setLongitude(dto.longitude());
        telemetria.setAltitudeMetros(dto.altitudeMetros());
        telemetria.setPrecisaoMetros(dto.precisaoMetros());
        telemetria.setAceleracaoX(dto.aceleracaoX());
        telemetria.setAceleracaoY(dto.aceleracaoY());
        telemetria.setAceleracaoZ(dto.aceleracaoZ());
        telemetria.setRotacaoX(dto.rotacaoX());
        telemetria.setRotacaoY(dto.rotacaoY());
        telemetria.setRotacaoZ(dto.rotacaoZ());
        telemetria.setQuedaDetectada(dto.quedaDetectada());

        telemetria.setRegistradoEm(Instant.now());

        Telemetria telemetriaSalva = telemetriaRepository.save(telemetria);

        if (Boolean.TRUE.equals(dto.quedaDetectada())) {
            Alerta alerta = new Alerta();
            alerta.setTipo(TipoAlerta.POSSIVEL_QUEDA); // se der vermelho aqui, confirme os valores dentro de TipoAlerta.java
            alerta.setStatus(StatusAlerta.PENDENTE);
            alerta.setGeradoEm(Instant.now());
            alerta.setTelemetriaId(telemetriaSalva.getId());
            alerta.setObservacao("Possível queda detectada pelos sensores de aceleração e giroscópio.");
            alertaRepository.save(alerta);
        }

        return converterParaResponseDTO(telemetriaSalva);
    }

    public List<TelemetriaResponseDTO> buscarPorDispositivo(String dispositivoId) {
        return telemetriaRepository.findAll().stream()
                .filter(t -> dispositivoId.equals(t.getDispositivoId()))
                .map(this::converterParaResponseDTO)
                .toList();
    }

    private TelemetriaResponseDTO converterParaResponseDTO(Telemetria telemetria) {
        return new TelemetriaResponseDTO(
                telemetria.getId(),
                telemetria.getDispositivoId(),
                telemetria.getPulseiraId(),
                telemetria.getPessoaMonitoradaId(),
                telemetria.getRegistradoEm(),
                telemetria.getLatitude(),
                telemetria.getLongitude(),
                telemetria.getAltitudeMetros(),
                telemetria.getPrecisaoMetros(),
                telemetria.getAceleracaoX(),
                telemetria.getAceleracaoY(),
                telemetria.getAceleracaoZ(),
                telemetria.getRotacaoX(),
                telemetria.getRotacaoY(),
                telemetria.getRotacaoZ(),
                telemetria.getQuedaDetectada()
        );
    }

}
