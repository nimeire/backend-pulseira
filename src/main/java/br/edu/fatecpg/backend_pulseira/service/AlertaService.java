package br.edu.fatecpg.backend_pulseira.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import br.edu.fatecpg.backend_pulseira.dto.PaginaDTO;
import br.edu.fatecpg.backend_pulseira.dto.ResumoAlertasDTO;
import br.edu.fatecpg.backend_pulseira.model.Alerta;
import br.edu.fatecpg.backend_pulseira.model.StatusAlerta;
import br.edu.fatecpg.backend_pulseira.repository.AlertaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private static final int TAMANHO_MAXIMO_PAGINA = 50;
    private static final Duration JANELA_DO_DIA = Duration.ofHours(24);

    private final AlertaRepository alertaRepository;
    private final VinculoCuidadorService vinculoService;

    public List<Alerta> listarPorPessoa(String cuidadorId, String pessoaId) {
        vinculoService.exigirAcessoAtivo(cuidadorId, pessoaId);
        return alertaRepository.findByPessoaMonitoradaIdOrderByGeradoEmDesc(pessoaId);
    }

    /** Para quem já teve o acesso verificado (ver AcessoService). */
    public List<Alerta> listarDaPessoa(String pessoaId) {
        return alertaRepository.findByPessoaMonitoradaIdOrderByGeradoEmDesc(pessoaId);
    }

    /**
     * Página de alertas, do mais recente para o mais antigo. {@code pagina}
     * começa em 1. Com {@code ultimas24h}, só entram os das últimas 24 horas.
     */
    public PaginaDTO<Alerta> pagina(String pessoaId, int pagina, int tamanho, boolean ultimas24h) {
        int numero = Math.max(pagina, 1) - 1;
        int itens = Math.min(Math.max(tamanho, 1), TAMANHO_MAXIMO_PAGINA);
        PageRequest paginacao = PageRequest.of(numero, itens, Sort.by(Sort.Direction.DESC, "geradoEm"));

        Page<Alerta> resultado = ultimas24h
                ? alertaRepository.findByPessoaMonitoradaIdAndGeradoEmGreaterThanEqual(
                        pessoaId, Instant.now().minus(JANELA_DO_DIA), paginacao)
                : alertaRepository.findByPessoaMonitoradaId(pessoaId, paginacao);
        return PaginaDTO.de(resultado);
    }

    public ResumoAlertasDTO resumo(String pessoaId) {
        long pendentes = alertaRepository.countByPessoaMonitoradaIdAndStatus(pessoaId, StatusAlerta.PENDENTE);
        Alerta ultimo = alertaRepository.findFirstByPessoaMonitoradaIdOrderByGeradoEmDesc(pessoaId).orElse(null);
        return new ResumoAlertasDTO(pendentes, ultimo);
    }

    /**
     * A pulseira deve manter o alarme enquanto o alerta mais recente da pessoa
     * ainda não foi resolvido nem marcado como falso positivo.
     */
    public boolean alarmeAtivo(String pessoaId) {
        return alertaRepository.findFirstByPessoaMonitoradaIdOrderByGeradoEmDesc(pessoaId)
                .map(alerta -> alerta.getStatus() == StatusAlerta.PENDENTE
                        || alerta.getStatus() == StatusAlerta.RECONHECIDO)
                .orElse(false);
    }

    public Alerta reconhecer(String alertaId, String cuidadorId, String observacao) {
        Alerta alerta = buscarComAcesso(alertaId, cuidadorId);

        if (alerta.getStatus() != StatusAlerta.PENDENTE) {
            throw new IllegalStateException("Somente alertas pendentes podem ser reconhecidos.");
        }

        alerta.setStatus(StatusAlerta.RECONHECIDO);
        alerta.setCuidadorReconheceuId(cuidadorId);
        alerta.setReconhecidoEm(Instant.now());
        alerta.setObservacao(observacao);
        return alertaRepository.save(alerta);
    }

    public Alerta resolver(String alertaId, String cuidadorId, String observacao) {
        Alerta alerta = buscarComAcesso(alertaId, cuidadorId);

        if (alerta.getStatus() != StatusAlerta.RECONHECIDO) {
            throw new IllegalStateException("O alerta deve ser reconhecido antes de ser resolvido.");
        }

        alerta.setStatus(StatusAlerta.RESOLVIDO);
        alerta.setResolvidoEm(Instant.now());
        alerta.setObservacao(observacao);
        return alertaRepository.save(alerta);
    }

    public Alerta marcarFalsoPositivo(String alertaId, String cuidadorId, String observacao) {
        Alerta alerta = buscarComAcesso(alertaId, cuidadorId);

        if (alerta.getStatus() == StatusAlerta.RESOLVIDO || alerta.getStatus() == StatusAlerta.FALSO_POSITIVO) {
            throw new IllegalStateException("Este alerta já foi finalizado.");
        }

        alerta.setStatus(StatusAlerta.FALSO_POSITIVO);
        alerta.setResolvidoEm(Instant.now());
        alerta.setObservacao(observacao);
        return alertaRepository.save(alerta);
    }

    private Alerta buscarComAcesso(String alertaId, String cuidadorId) {
        Alerta alerta = alertaRepository.findById(alertaId)
                .orElseThrow(() -> new IllegalArgumentException("Alerta não encontrado."));
        vinculoService.exigirAcessoAtivo(cuidadorId, alerta.getPessoaMonitoradaId());
        return alerta;
    }
}
