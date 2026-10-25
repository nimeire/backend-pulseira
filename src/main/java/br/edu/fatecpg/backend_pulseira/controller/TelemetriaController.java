package br.edu.fatecpg.backend_pulseira.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecpg.backend_pulseira.model.Telemetria;
import br.edu.fatecpg.backend_pulseira.service.TelemetriaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Entrada HTTP usada pela pulseira para registrar uma leitura de telemetria.
 */
@Slf4j
@RestController
@RequestMapping("/api/telemetrias")
@RequiredArgsConstructor
public class TelemetriaController {

    private final TelemetriaService telemetriaService;

    @PostMapping
    public ResponseEntity<Telemetria> registrar(@RequestBody Telemetria telemetria) {
        Telemetria salva = telemetriaService.registrar(telemetria);
        if (Boolean.TRUE.equals(salva.getQuedaDetectada())) {
            log.warn(
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
