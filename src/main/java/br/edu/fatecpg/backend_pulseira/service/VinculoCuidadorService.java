package br.edu.fatecpg.backend_pulseira.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.fatecpg.backend_pulseira.model.NivelAcesso;
import br.edu.fatecpg.backend_pulseira.model.VinculoCuidador;
import br.edu.fatecpg.backend_pulseira.repository.VinculoCuidadorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VinculoCuidadorService {

    private final VinculoCuidadorRepository vinculoRepository;
    private final CuidadorService cuidadorService;
    private final PessoaMonitoradaService pessoaService;

    public VinculoCuidador vincular(String cuidadorId, String pessoaId, NivelAcesso nivelAcesso) {
        cuidadorService.buscarAtivo(cuidadorId);
        pessoaService.buscarAtiva(pessoaId);

        // Há um índice único (cuidador, pessoa): um vínculo desfeito é reativado, não duplicado.
        VinculoCuidador vinculo = vinculoRepository
                .findByCuidadorIdAndPessoaMonitoradaId(cuidadorId, pessoaId)
                .orElseGet(VinculoCuidador::new);

        if (Boolean.TRUE.equals(vinculo.getAtivo())) {
            throw new IllegalArgumentException("O cuidador já possui vínculo ativo com esta pessoa.");
        }

        vinculo.setCuidadorId(cuidadorId);
        vinculo.setPessoaMonitoradaId(pessoaId);
        vinculo.setNivelAcesso(nivelAcesso == null ? NivelAcesso.LEITURA : nivelAcesso);
        vinculo.setAtivo(true);
        vinculo.setCriadoEm(Instant.now());
        return vinculoRepository.save(vinculo);
    }

    public void desvincular(String cuidadorId, String pessoaId) {
        VinculoCuidador vinculo = vinculoRepository
                .findByCuidadorIdAndPessoaMonitoradaId(cuidadorId, pessoaId)
                .filter(v -> Boolean.TRUE.equals(v.getAtivo()))
                .orElseThrow(() -> new IllegalArgumentException("Vínculo não encontrado."));
        vinculo.setAtivo(false);
        vinculoRepository.save(vinculo);
    }

    public List<VinculoCuidador> listarAtivosDoCuidador(String cuidadorId) {
        return vinculoRepository.findByCuidadorIdAndAtivoTrue(cuidadorId);
    }

    public List<VinculoCuidador> listarAtivosDaPessoa(String pessoaId) {
        return vinculoRepository.findByPessoaMonitoradaIdAndAtivoTrue(pessoaId);
    }

    public void exigirAcessoAtivo(String cuidadorId, String pessoaId) {
        if (!vinculoRepository.existsByCuidadorIdAndPessoaMonitoradaIdAndAtivoTrue(cuidadorId, pessoaId)) {
            throw new IllegalStateException("O cuidador não possui acesso a esta pessoa monitorada.");
        }
    }
}
