package br.edu.fatecpg.backend_pulseira.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

import br.edu.fatecpg.backend_pulseira.model.NivelAcesso;
import br.edu.fatecpg.backend_pulseira.model.VinculoCuidador;
import br.edu.fatecpg.backend_pulseira.repository.VinculoCuidadorRepository;

@Service
public class VinculoCuidadorService {

    private final VinculoCuidadorRepository vinculoRepository;
    private final CuidadorService cuidadorService;
    private final PessoaMonitoradaService pessoaService;

    public VinculoCuidadorService(VinculoCuidadorRepository vinculoRepository,
            CuidadorService cuidadorService, PessoaMonitoradaService pessoaService) {
        this.vinculoRepository = vinculoRepository;
        this.cuidadorService = cuidadorService;
        this.pessoaService = pessoaService;
    }

    public VinculoCuidador vincular(String cuidadorId, String pessoaId, NivelAcesso nivelAcesso) {
        cuidadorService.buscarAtivo(cuidadorId);
        pessoaService.buscarAtiva(pessoaId);
        if (vinculoRepository.existsByCuidadorIdAndPessoaMonitoradaIdAndAtivoTrue(cuidadorId, pessoaId)) {
            throw new IllegalArgumentException("O cuidador já possui vínculo ativo com esta pessoa.");
        }

        VinculoCuidador vinculo = new VinculoCuidador();
        vinculo.setCuidadorId(cuidadorId);
        vinculo.setPessoaMonitoradaId(pessoaId);
        vinculo.setNivelAcesso(nivelAcesso == null ? NivelAcesso.LEITURA : nivelAcesso);
        vinculo.setAtivo(true);
        vinculo.setCriadoEm(Instant.now());
        return vinculoRepository.save(vinculo);
    }

    public void exigirAcessoAtivo(String cuidadorId, String pessoaId) {
        if (!vinculoRepository.existsByCuidadorIdAndPessoaMonitoradaIdAndAtivoTrue(cuidadorId, pessoaId)) {
            throw new IllegalStateException("O cuidador não possui acesso a esta pessoa monitorada.");
        }
    }
}
