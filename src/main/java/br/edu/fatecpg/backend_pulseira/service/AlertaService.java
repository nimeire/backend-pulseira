package br.edu.fatecpg.backend_pulseira.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.fatecpg.backend_pulseira.model.Alerta;
import br.edu.fatecpg.backend_pulseira.model.StatusAlerta;
import br.edu.fatecpg.backend_pulseira.repository.AlertaRepository;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final VinculoCuidadorService vinculoService;

    public AlertaService(AlertaRepository alertaRepository, VinculoCuidadorService vinculoService) {
        this.alertaRepository = alertaRepository;
        this.vinculoService = vinculoService;
    }

    public List<Alerta> listarPorPessoa(String cuidadorId, String pessoaId) {
        vinculoService.exigirAcessoAtivo(cuidadorId, pessoaId);
        return alertaRepository.findByPessoaMonitoradaIdOrderByGeradoEmDesc(pessoaId);
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
