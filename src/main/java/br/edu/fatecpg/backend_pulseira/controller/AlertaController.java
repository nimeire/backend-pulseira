package br.edu.fatecpg.backend_pulseira.controller;

import br.edu.fatecpg.backend_pulseira.dto.AlertaResponseDTO;
import br.edu.fatecpg.backend_pulseira.dto.AtualizarAlertaRequestDTO;
import br.edu.fatecpg.backend_pulseira.model.Alerta;
import br.edu.fatecpg.backend_pulseira.service.AlertaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;

    // Consulta histórico de alertas do idoso validando o vínculo do cuidador
    @GetMapping("/pessoa/{pessoaId}")
    public ResponseEntity<List<AlertaResponseDTO>> listarPorPessoa(
            @PathVariable String pessoaId,
            @RequestParam String cuidadorId) {
        List<Alerta> alertas = alertaService.listarPorPessoa(cuidadorId, pessoaId);
        List<AlertaResponseDTO> dtos = alertas.stream()
                .map(AlertaResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // Cuidador assume o atendimento (status RECONHECIDO)
    @PatchMapping("/{alertaId}/reconhecer")
    public ResponseEntity<AlertaResponseDTO> reconhecer(
            @PathVariable String alertaId,
            @RequestBody AtualizarAlertaRequestDTO dto) {
        Alerta alerta = alertaService.reconhecer(alertaId, dto.cuidadorId(), dto.observacao());
        return ResponseEntity.ok(AlertaResponseDTO.fromEntity(alerta));
    }

    // Cuidador encerra o atendimento com socorro concluído (status RESOLVIDO)
    @PatchMapping("/{alertaId}/resolver")
    public ResponseEntity<AlertaResponseDTO> resolver(
            @PathVariable String alertaId,
            @RequestBody AtualizarAlertaRequestDTO dto) {
        Alerta alerta = alertaService.resolver(alertaId, dto.cuidadorId(), dto.observacao());
        return ResponseEntity.ok(AlertaResponseDTO.fromEntity(alerta));
    }

    // Marca como falso alarme, ex: tchau forte ou choque de panela (status FALSO_POSITIVO)
    @PatchMapping("/{alertaId}/falso-positivo")
    public ResponseEntity<AlertaResponseDTO> marcarFalsoPositivo(
            @PathVariable String alertaId,
            @RequestBody AtualizarAlertaRequestDTO dto) {
        Alerta alerta = alertaService.marcarFalsoPositivo(alertaId, dto.cuidadorId(), dto.observacao());
        return ResponseEntity.ok(AlertaResponseDTO.fromEntity(alerta));
    }
}