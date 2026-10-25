package br.edu.fatecpg.backend_pulseira.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecpg.backend_pulseira.dto.VinculoPulseiraRequestDTO;
import br.edu.fatecpg.backend_pulseira.model.Pulseira;
import br.edu.fatecpg.backend_pulseira.service.PulseiraService;

import lombok.RequiredArgsConstructor;

/** Cadastro e ativação da pulseira física para o protótipo. */
@RestController
@RequestMapping("/api/pulseiras")
@RequiredArgsConstructor
public class PulseiraController {

    private final PulseiraService pulseiraService;

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
