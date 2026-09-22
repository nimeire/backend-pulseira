package br.edu.fatecpg.backend_pulseira.controller;

import br.edu.fatecpg.backend_pulseira.dto.PulseiraRequestDTO;
import br.edu.fatecpg.backend_pulseira.dto.PulseiraResponseDTO;
import br.edu.fatecpg.backend_pulseira.model.Pulseira;
import br.edu.fatecpg.backend_pulseira.service.PulseiraService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pulseiras")
public class PulseiraController {

    private final PulseiraService pulseiraService;

    public PulseiraController(PulseiraService pulseiraService) {
        this.pulseiraService = pulseiraService;
    }

    @PostMapping
    public ResponseEntity<PulseiraResponseDTO> cadastrar(@Valid @RequestBody PulseiraRequestDTO dto) {
        Pulseira pulseira = new Pulseira();
        pulseira.setDispositivoId(dto.dispositivoId());

        Pulseira salva = pulseiraService.cadastrar(pulseira);
        return ResponseEntity.status(HttpStatus.CREATED).body(PulseiraResponseDTO.fromEntity(salva));
    }

    @PatchMapping("/{pulseiraId}/vincular/{pessoaId}")
    public ResponseEntity<PulseiraResponseDTO> vincular(
            @PathVariable String pulseiraId,
            @PathVariable String pessoaId) {
        Pulseira vinculada = pulseiraService.vincular(pulseiraId, pessoaId);
        return ResponseEntity.ok(PulseiraResponseDTO.fromEntity(vinculada));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PulseiraResponseDTO> buscarPorId(@PathVariable String id) {
        Pulseira pulseira = pulseiraService.buscarPorId(id);
        return ResponseEntity.ok(PulseiraResponseDTO.fromEntity(pulseira));
    }

    @GetMapping("/dispositivo/{dispositivoId}")
    public ResponseEntity<PulseiraResponseDTO> buscarPorDispositivo(@PathVariable String dispositivoId) {
        Pulseira pulseira = pulseiraService.buscarPorDispositivo(dispositivoId);
        return ResponseEntity.ok(PulseiraResponseDTO.fromEntity(pulseira));
    }
}