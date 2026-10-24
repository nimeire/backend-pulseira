package br.edu.fatecpg.backend_pulseira.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecpg.backend_pulseira.model.Telemetria;
import br.edu.fatecpg.backend_pulseira.service.TelemetriaService;

/**
 * Entrada HTTP usada pela pulseira para registrar uma leitura de telemetria.
 */
@RestController
@RequestMapping("/api/telemetrias")
public class TelemetriaController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelemetriaController.class);

    private final TelemetriaService telemetriaService;

    public TelemetriaController(TelemetriaService telemetriaService) {
        this.telemetriaService = telemetriaService;
    }

    @PostMapping
    public ResponseEntity<Telemetria> registrar(@RequestBody Telemetria telemetria) {
        Telemetria salva = telemetriaService.registrar(telemetria);
        if (Boolean.TRUE.equals(salva.getQuedaDetectada())) {
            LOGGER.warn(
                    "POSSIVEL QUEDA | dispositivo={} | GPS=({}, {}) | telemetriaId={}",
                    salva.getDispositivoId(),
                    salva.getLatitude(),
                    salva.getLongitude(),
                    salva.getId());
        }
        URI endereco = URI.create("/api/telemetrias/" + salva.getId());
        return ResponseEntity.created(endereco).body(salva);
    }
}
