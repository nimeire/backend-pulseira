package br.edu.fatecpg.backend_pulseira.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.edu.fatecpg.backend_pulseira.model.PessoaMonitorada;
import br.edu.fatecpg.backend_pulseira.model.Pulseira;
import br.edu.fatecpg.backend_pulseira.model.StatusPulseira;
import br.edu.fatecpg.backend_pulseira.repository.PulseiraRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PulseiraService {

    private final PulseiraRepository pulseiraRepository;
    private final PessoaMonitoradaService pessoaService;

    public Pulseira cadastrar(Pulseira pulseira) {
        if (pulseira.getDispositivoId() == null || pulseira.getDispositivoId().isBlank()) {
            throw new IllegalArgumentException("O identificador do dispositivo é obrigatório.");
        }
        if (pulseiraRepository.findByDispositivoId(pulseira.getDispositivoId().trim()).isPresent()) {
            throw new IllegalArgumentException("Já existe uma pulseira com este dispositivo.");
        }
        pulseira.setDispositivoId(pulseira.getDispositivoId().trim());
        pulseira.setStatus(StatusPulseira.INATIVA);
        pulseira.setAtualizadaEm(Instant.now());
        return pulseiraRepository.save(pulseira);
    }

    public Pulseira vincular(String pulseiraId, String pessoaId) {
        Pulseira pulseira = buscarPorId(pulseiraId);
        PessoaMonitorada pessoa = pessoaService.buscarAtiva(pessoaId);

        if (pulseira.getPessoaMonitoradaId() != null) {
            throw new IllegalStateException("A pulseira já está vinculada a uma pessoa.");
        }

        Instant agora = Instant.now();
        pulseira.setPessoaMonitoradaId(pessoa.getId());
        pulseira.setStatus(StatusPulseira.ATIVA);
        pulseira.setVinculadaEm(agora);
        pulseira.setAtualizadaEm(agora);
        return pulseiraRepository.save(pulseira);
    }

    /** Pareamento feito pela própria pessoa, a partir do código gravado na pulseira. */
    public Pulseira parear(String dispositivoId, String pessoaId) {
        if (dispositivoId == null || dispositivoId.isBlank()) {
            throw new IllegalArgumentException("Informe o código do dispositivo.");
        }
        Pulseira pulseira = buscarPorDispositivo(dispositivoId.trim());
        return vincular(pulseira.getId(), pessoaId);
    }

    public Pulseira desvincular(Pulseira pulseira) {
        Instant agora = Instant.now();
        pulseira.setPessoaMonitoradaId(null);
        pulseira.setStatus(StatusPulseira.INATIVA);
        pulseira.setVinculadaEm(null);
        pulseira.setAtualizadaEm(agora);
        return pulseiraRepository.save(pulseira);
    }

    public void registrarSinal(Pulseira pulseira) {
        pulseira.setUltimoSinalEm(Instant.now());
        pulseiraRepository.save(pulseira);
    }

    public Optional<Pulseira> buscarPorPessoa(String pessoaId) {
        return pulseiraRepository.findByPessoaMonitoradaId(pessoaId).stream().findFirst();
    }

    public List<Pulseira> listarTodas() {
        return pulseiraRepository.findAll();
    }

    public Pulseira buscarPorDispositivo(String dispositivoId) {
        return pulseiraRepository.findByDispositivoId(dispositivoId)
                .orElseThrow(() -> new IllegalArgumentException("Pulseira não encontrada."));
    }

    public Pulseira buscarPorId(String pulseiraId) {
        return pulseiraRepository.findById(pulseiraId)
                .orElseThrow(() -> new IllegalArgumentException("Pulseira não encontrada."));
    }
}
