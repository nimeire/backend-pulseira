package br.edu.fatecpg.backend_pulseira.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecpg.backend_pulseira.dto.AlertaAcaoRequestDTO;
import br.edu.fatecpg.backend_pulseira.dto.PaginaDTO;
import br.edu.fatecpg.backend_pulseira.dto.ResumoAlertasDTO;
import br.edu.fatecpg.backend_pulseira.model.Alerta;
import br.edu.fatecpg.backend_pulseira.model.PerfilConta;
import br.edu.fatecpg.backend_pulseira.security.ContaAutenticada;
import br.edu.fatecpg.backend_pulseira.service.AcessoService;
import br.edu.fatecpg.backend_pulseira.service.AlertaService;

import lombok.RequiredArgsConstructor;

/**
 * Consulta e atendimento dos alertas. Quem age é sempre a conta autenticada:
 * o {@code cuidadorId} que antes vinha na requisição é ignorado. Ao resolver um
 * alerta, a pulseira percebe pela consulta de alarme e para a buzina e o LED.
 */
@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;
    private final AcessoService acessoService;

    @GetMapping
    public List<Alerta> listar(
            @AuthenticationPrincipal ContaAutenticada conta,
            @RequestParam String pessoaMonitoradaId) {
        acessoService.exigirLeitura(conta, pessoaMonitoradaId);
        return alertaService.listarDaPessoa(pessoaMonitoradaId);
    }

    /** Alertas em páginas (1, 2, 3...). `ultimas24h=true` mostra só o dia; sem isso, todos. */
    @GetMapping("/pagina")
    public PaginaDTO<Alerta> pagina(
            @AuthenticationPrincipal ContaAutenticada conta,
            @RequestParam String pessoaMonitoradaId,
            @RequestParam(defaultValue = "1") int pagina,
            @RequestParam(defaultValue = "5") int tamanho,
            @RequestParam(defaultValue = "false") boolean ultimas24h) {
        acessoService.exigirLeitura(conta, pessoaMonitoradaId);
        return alertaService.pagina(pessoaMonitoradaId, pagina, tamanho, ultimas24h);
    }

    /** Quantos alertas esperam atendimento e qual foi o mais recente. */
    @GetMapping("/resumo")
    public ResumoAlertasDTO resumo(
            @AuthenticationPrincipal ContaAutenticada conta,
            @RequestParam String pessoaMonitoradaId) {
        acessoService.exigirLeitura(conta, pessoaMonitoradaId);
        return alertaService.resumo(pessoaMonitoradaId);
    }

    @PutMapping("/{alertaId}/reconhecer")
    public Alerta reconhecer(
            @AuthenticationPrincipal ContaAutenticada conta,
            @PathVariable String alertaId,
            @RequestBody(required = false) AlertaAcaoRequestDTO requisicao) {
        acessoService.exigirPerfil(conta, PerfilConta.RESPONSAVEL);
        return alertaService.reconhecer(alertaId, conta.getId(), observacao(requisicao));
    }

    @PutMapping("/{alertaId}/resolver")
    public Alerta resolver(
            @AuthenticationPrincipal ContaAutenticada conta,
            @PathVariable String alertaId,
            @RequestBody(required = false) AlertaAcaoRequestDTO requisicao) {
        acessoService.exigirPerfil(conta, PerfilConta.RESPONSAVEL);
        return alertaService.resolver(alertaId, conta.getId(), observacao(requisicao));
    }

    @PutMapping("/{alertaId}/falso-positivo")
    public Alerta marcarFalsoPositivo(
            @AuthenticationPrincipal ContaAutenticada conta,
            @PathVariable String alertaId,
            @RequestBody(required = false) AlertaAcaoRequestDTO requisicao) {
        acessoService.exigirPerfil(conta, PerfilConta.RESPONSAVEL);
        return alertaService.marcarFalsoPositivo(alertaId, conta.getId(), observacao(requisicao));
    }

    private String observacao(AlertaAcaoRequestDTO requisicao) {
        return requisicao == null ? null : requisicao.observacao();
    }
}
