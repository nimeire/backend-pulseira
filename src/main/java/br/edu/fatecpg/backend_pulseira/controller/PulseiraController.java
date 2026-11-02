package br.edu.fatecpg.backend_pulseira.controller;

import java.net.URI;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecpg.backend_pulseira.dto.VinculoPulseiraRequestDTO;
import br.edu.fatecpg.backend_pulseira.model.Pulseira;
import br.edu.fatecpg.backend_pulseira.service.AlertaService;
import br.edu.fatecpg.backend_pulseira.service.PulseiraService;

import lombok.RequiredArgsConstructor;

/** Cadastro e ativação da pulseira física para o protótipo. */
@RestController
@RequestMapping("/api/pulseiras")
@RequiredArgsConstructor
public class PulseiraController {

    private final PulseiraService pulseiraService;
    private final AlertaService alertaService;

    /** Consultado pela pulseira durante o alarme; false indica que o front resolveu o alerta. */
    @GetMapping("/{dispositivoId}/alarme")
    public Map<String, Boolean> consultarAlarme(@PathVariable String dispositivoId) {
        Pulseira pulseira = pulseiraService.buscarPorDispositivo(dispositivoId.trim());
        String pessoaId = pulseira.getPessoaMonitoradaId();
        return Map.of("alarmeAtivo", pessoaId != null && alertaService.alarmeAtivo(pessoaId));
    }

    @PostMapping
    public ResponseEntity<Pulseira> cadastrar(@RequestBody Pulseira pulseira) {
        Pulseira salva = pulseiraService.cadastrar(pulseira);
        return ResponseEntity.created(URI.create("/api/pulseiras/" + salva.getId())).body(salva);
    }

    @PutMapping("/{pulseiraId}/vinculo")
    public ResponseEntity<Pulseira> vincular(
            @PathVariable String pulseiraId,
            @RequestBody VinculoPulseiraRequestDTO requisicao) {
        if (requisicao.pessoaMonitoradaId() == null || requisicao.pessoaMonitoradaId().isBlank()) {
            throw new IllegalArgumentException("O ID da pessoa monitorada é obrigatório.");
        }
        return ResponseEntity.ok(pulseiraService.vincular(pulseiraId, requisicao.pessoaMonitoradaId()));
    }
}
