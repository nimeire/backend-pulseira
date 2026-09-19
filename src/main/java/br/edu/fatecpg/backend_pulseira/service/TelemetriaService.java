package br.edu.fatecpg.backend_pulseira.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import br.edu.fatecpg.backend_pulseira.model.Alerta;
import br.edu.fatecpg.backend_pulseira.model.Pulseira;
import br.edu.fatecpg.backend_pulseira.model.StatusAlerta;
import br.edu.fatecpg.backend_pulseira.model.StatusPulseira;
import br.edu.fatecpg.backend_pulseira.model.Telemetria;
import br.edu.fatecpg.backend_pulseira.model.TipoAlerta;
import br.edu.fatecpg.backend_pulseira.repository.AlertaRepository;
import br.edu.fatecpg.backend_pulseira.repository.TelemetriaRepository;

@Service
public class TelemetriaService {

    private final TelemetriaRepository telemetriaRepository;
    private final AlertaRepository alertaRepository;
    private final PulseiraService pulseiraService;
    private final PessoaMonitoradaService pessoaService;

    public TelemetriaService(TelemetriaRepository telemetriaRepository,
            AlertaRepository alertaRepository, PulseiraService pulseiraService,
            PessoaMonitoradaService pessoaService) {
        this.telemetriaRepository = telemetriaRepository;
        this.alertaRepository = alertaRepository;
        this.pulseiraService = pulseiraService;
        this.pessoaService = pessoaService;
    }

    public Telemetria registrar(Telemetria telemetria) {
        if (telemetria.getDispositivoId() == null || telemetria.getDispositivoId().isBlank()) {
            throw new IllegalArgumentException("O identificador do dispositivo é obrigatório.");
        }
        validarCoordenada(telemetria.getLatitude(), -90, 90, "latitude");
        validarCoordenada(telemetria.getLongitude(), -180, 180, "longitude");

        Pulseira pulseira = pulseiraService.buscarPorDispositivo(telemetria.getDispositivoId().trim());
        if (pulseira.getStatus() != StatusPulseira.ATIVA || pulseira.getPessoaMonitoradaId() == null) {
            throw new IllegalStateException("A pulseira precisa estar ativa e vinculada para enviar telemetria.");
        }
        pessoaService.buscarAtiva(pulseira.getPessoaMonitoradaId());

        // Os vínculos vêm do cadastro da pulseira, nunca do dispositivo externo.
        telemetria.setDispositivoId(pulseira.getDispositivoId());
        telemetria.setPulseiraId(pulseira.getId());
        telemetria.setPessoaMonitoradaId(pulseira.getPessoaMonitoradaId());
        telemetria.setRegistradoEm(telemetria.getRegistradoEm() == null ? Instant.now() : telemetria.getRegistradoEm());

        Telemetria salva = telemetriaRepository.save(telemetria);
        if (Boolean.TRUE.equals(salva.getQuedaDetectada())) {
            gerarAlertaDeQueda(salva);
        }
        return salva;
    }

    private void gerarAlertaDeQueda(Telemetria telemetria) {
        Alerta alerta = new Alerta();
        alerta.setTipo(TipoAlerta.POSSIVEL_QUEDA);
        alerta.setStatus(StatusAlerta.PENDENTE);
        alerta.setPessoaMonitoradaId(telemetria.getPessoaMonitoradaId());
        alerta.setPulseiraId(telemetria.getPulseiraId());
        alerta.setTelemetriaId(telemetria.getId());
        alerta.setGeradoEm(Instant.now());
        alertaRepository.save(alerta);
    }

    private void validarCoordenada(Double valor, double minimo, double maximo, String nome) {
        if (valor != null && (valor < minimo || valor > maximo)) {
            throw new IllegalArgumentException("Valor de " + nome + " inválido.");
        }
    }
}
