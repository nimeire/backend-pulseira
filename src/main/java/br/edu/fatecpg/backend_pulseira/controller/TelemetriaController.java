package br.edu.fatecpg.backend_pulseira.controller;

import br.edu.fatecpg.backend_pulseira.dto.TelemetriaRequestDTO;
import br.edu.fatecpg.backend_pulseira.dto.TelemetriaResponseDTO;
import br.edu.fatecpg.backend_pulseira.service.TelemetriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/telemetrias")

public class TelemetriaController {

    private final TelemetriaService telemetriaService;
    public TelemetriaController(TelemetriaService telemetriaService){
        this.telemetriaService= telemetriaService;
    }

    @PostMapping
    public ResponseEntity<TelemetriaResponseDTO> receberTelemetria(@RequestBody TelemetriaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("/{dispositivoId}")
    public ResponseEntity<List<TelemetriaResponseDTO>> buscarPorDispositivos(@PathVariable String dispositivoId) {

        return ResponseEntity.ok(List.of());

    }
}
