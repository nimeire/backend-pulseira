package br.edu.fatecpg.backend_pulseira.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.fatecpg.backend_pulseira.dto.PulseiraAdminDTO;
import br.edu.fatecpg.backend_pulseira.dto.UsuarioAdminDTO;
import br.edu.fatecpg.backend_pulseira.model.Cuidador;
import br.edu.fatecpg.backend_pulseira.model.PerfilConta;
import br.edu.fatecpg.backend_pulseira.model.PessoaMonitorada;
import br.edu.fatecpg.backend_pulseira.repository.CuidadorRepository;
import br.edu.fatecpg.backend_pulseira.repository.PessoaMonitoradaRepository;
import br.edu.fatecpg.backend_pulseira.repository.VinculoCuidadorRepository;

import lombok.RequiredArgsConstructor;

/** Operações da tela de administração. */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final CuidadorRepository cuidadorRepository;
    private final PessoaMonitoradaRepository pessoaRepository;
    private final VinculoCuidadorRepository vinculoRepository;
    private final PulseiraService pulseiraService;

    public List<UsuarioAdminDTO> listarUsuarios() {
        return cuidadorRepository.findAll().stream()
                .filter(conta -> conta.getPerfil() != PerfilConta.ADMIN)
                .map(UsuarioAdminDTO::de)
                .toList();
    }

    public UsuarioAdminDTO alterarStatus(String contaId, boolean ativo) {
        Cuidador conta = buscarNaoAdmin(contaId);
        conta.setAtivo(ativo);
        return UsuarioAdminDTO.de(cuidadorRepository.save(conta));
    }

    public void excluir(String contaId) {
        Cuidador conta = buscarNaoAdmin(contaId);
        vinculoRepository.deleteByCuidadorId(contaId);

        if (conta.getPerfil() == PerfilConta.PACIENTE && conta.getPessoaMonitoradaId() != null) {
            pessoaRepository.findById(conta.getPessoaMonitoradaId()).ifPresent(pessoa -> {
                pulseiraService.buscarPorPessoa(pessoa.getId()).ifPresent(pulseiraService::desvincular);
                pessoa.setAtiva(false);
                pessoaRepository.save(pessoa);
            });
        }

        cuidadorRepository.deleteById(contaId);
    }

    public List<PulseiraAdminDTO> listarPulseiras() {
        return pulseiraService.listarTodas().stream()
                .map(pulseira -> new PulseiraAdminDTO(
                        pulseira.getId(),
                        pulseira.getDispositivoId(),
                        pulseira.getStatus(),
                        nomeDaPessoa(pulseira.getPessoaMonitoradaId())))
                .toList();
    }

    private String nomeDaPessoa(String pessoaId) {
        if (pessoaId == null) {
            return null;
        }
        return pessoaRepository.findById(pessoaId).map(PessoaMonitorada::getNome).orElse(null);
    }

    private Cuidador buscarNaoAdmin(String contaId) {
        Cuidador conta = cuidadorRepository.findById(contaId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        if (conta.getPerfil() == PerfilConta.ADMIN) {
            throw new IllegalArgumentException("Contas de administrador não podem ser alteradas por aqui.");
        }
        return conta;
    }
}
