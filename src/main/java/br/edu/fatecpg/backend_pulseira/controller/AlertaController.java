package br.edu.fatecpg.backend_pulseira.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecpg.backend_pulseira.dto.AlertaAcaoRequestDTO;
import br.edu.fatecpg.backend_pulseira.model.Alerta;
import br.edu.fatecpg.backend_pulseira.service.AlertaService;

import lombok.RequiredArgsConstructor;

/**
 * Atendimento dos alertas pelo cuidador. Ao resolver um alerta, a pulseira
 * percebe pela consulta de alarme e para a buzina e o LED.
 */
@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;

    @GetMapping
    public List<Alerta> listar(@RequestParam String cuidadorId, @RequestParam String pessoaMonitoradaId) {
        return alertaService.listarPorPessoa(cuidadorId, pessoaMonitoradaId);
    }

    @PutMapping("/{alertaId}/reconhecer")
    public Alerta reconhecer(@PathVariable String alertaId, @RequestBody AlertaAcaoRequestDTO requisicao) {
        return alertaService.reconhecer(alertaId, exigirCuidador(requisicao), requisicao.observacao());
    }

    @PutMapping("/{alertaId}/resolver")
    public Alerta resolver(@PathVariable String alertaId, @RequestBody AlertaAcaoRequestDTO requisicao) {
        return alertaService.resolver(alertaId, exigirCuidador(requisicao), requisicao.observacao());
    }

    @PutMapping("/{alertaId}/falso-positivo")
    public Alerta marcarFalsoPositivo(@PathVariable String alertaId, @RequestBody AlertaAcaoRequestDTO requisicao) {
        return alertaService.marcarFalsoPositivo(alertaId, exigirCuidador(requisicao), requisicao.observacao());
    }

    private String exigirCuidador(AlertaAcaoRequestDTO requisicao) {
        if (requisicao.cuidadorId() == null || requisicao.cuidadorId().isBlank()) {
            throw new IllegalArgumentException("O ID do cuidador é obrigatório.");
        }
        return requisicao.cuidadorId();
    }
}
