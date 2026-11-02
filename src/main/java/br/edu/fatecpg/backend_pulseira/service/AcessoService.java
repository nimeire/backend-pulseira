package br.edu.fatecpg.backend_pulseira.service;

import org.springframework.stereotype.Service;

import br.edu.fatecpg.backend_pulseira.model.PerfilConta;
import br.edu.fatecpg.backend_pulseira.repository.VinculoCuidadorRepository;
import br.edu.fatecpg.backend_pulseira.security.AcessoNegadoException;
import br.edu.fatecpg.backend_pulseira.security.ContaAutenticada;

import lombok.RequiredArgsConstructor;

/** Decide quem pode ver ou alterar os dados de uma pessoa monitorada. */
@Service
@RequiredArgsConstructor
public class AcessoService {

    private final VinculoCuidadorRepository vinculoRepository;

    public boolean ehDona(ContaAutenticada conta, String pessoaId) {
        return conta.getPerfil() == PerfilConta.PACIENTE
                && pessoaId != null
                && pessoaId.equals(conta.getPessoaMonitoradaId());
    }

    /** Leitura: administrador, a própria pessoa ou um responsável com vínculo ativo. */
    public void exigirLeitura(ContaAutenticada conta, String pessoaId) {
        if (conta.getPerfil() == PerfilConta.ADMIN || ehDona(conta, pessoaId)) {
            return;
        }
        if (conta.getPerfil() == PerfilConta.RESPONSAVEL
                && vinculoRepository.existsByCuidadorIdAndPessoaMonitoradaIdAndAtivoTrue(conta.getId(), pessoaId)) {
            return;
        }
        throw new AcessoNegadoException("Você não tem acesso a esta pessoa monitorada.");
    }

    /** Alteração: administrador ou a própria pessoa. */
    public void exigirDona(ContaAutenticada conta, String pessoaId) {
        if (conta.getPerfil() == PerfilConta.ADMIN || ehDona(conta, pessoaId)) {
            return;
        }
        throw new AcessoNegadoException("Somente a própria pessoa pode alterar estes dados.");
    }

    public void exigirPerfil(ContaAutenticada conta, PerfilConta esperado) {
        if (conta.getPerfil() != esperado) {
            throw new AcessoNegadoException("Esta ação não está disponível para o seu tipo de conta.");
        }
    }
}
