package br.edu.fatecpg.backend_pulseira.controller;

import java.net.URI;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecpg.backend_pulseira.dto.ParearPulseiraRequestDTO;
import br.edu.fatecpg.backend_pulseira.dto.VinculoPulseiraRequestDTO;
import br.edu.fatecpg.backend_pulseira.model.PerfilConta;
import br.edu.fatecpg.backend_pulseira.model.Pulseira;
import br.edu.fatecpg.backend_pulseira.security.ContaAutenticada;
import br.edu.fatecpg.backend_pulseira.service.AcessoService;
import br.edu.fatecpg.backend_pulseira.service.AlertaService;
import br.edu.fatecpg.backend_pulseira.service.PulseiraService;

import lombok.RequiredArgsConstructor;

/** Cadastro, pareamento e consultas da pulseira física. */
@RestController
@RequestMapping("/api/pulseiras")
@RequiredArgsConstructor
public class PulseiraController {

    private final PulseiraService pulseiraService;
    private final AlertaService alertaService;
    private final AcessoService acessoService;

    /** Consultado pela pulseira durante o alarme; false indica que o front resolveu o alerta. */
    @GetMapping("/{dispositivoId}/alarme")
    public Map<String, Boolean> consultarAlarme(@PathVariable String dispositivoId) {
        Pulseira pulseira = pulseiraService.buscarPorDispositivo(dispositivoId.trim());
        pulseiraService.registrarSinal(pulseira);
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

    /** O paciente conecta a própria pulseira pelo código do dispositivo. */
    @PostMapping("/parear")
    public Pulseira parear(
            @AuthenticationPrincipal ContaAutenticada conta,
            @RequestBody ParearPulseiraRequestDTO requisicao) {
        acessoService.exigirPerfil(conta, PerfilConta.PACIENTE);
        return pulseiraService.parear(requisicao.dispositivoId(), conta.getPessoaMonitoradaId());
    }

    /** O paciente desconecta a própria pulseira. */
    @DeleteMapping("/{pulseiraId}/vinculo")
    public Pulseira desparear(
            @AuthenticationPrincipal ContaAutenticada conta,
            @PathVariable String pulseiraId) {
        Pulseira pulseira = pulseiraService.buscarPorId(pulseiraId);
        if (pulseira.getPessoaMonitoradaId() == null) {
            throw new IllegalArgumentException("A pulseira não está vinculada a ninguém.");
        }
        acessoService.exigirDona(conta, pulseira.getPessoaMonitoradaId());
        return pulseiraService.desvincular(pulseira);
    }
}
