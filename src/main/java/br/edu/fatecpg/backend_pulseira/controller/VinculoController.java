package br.edu.fatecpg.backend_pulseira.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecpg.backend_pulseira.dto.PessoaResumoDTO;
import br.edu.fatecpg.backend_pulseira.dto.VinculoCodigoRequestDTO;
import br.edu.fatecpg.backend_pulseira.model.NivelAcesso;
import br.edu.fatecpg.backend_pulseira.model.PerfilConta;
import br.edu.fatecpg.backend_pulseira.model.PessoaMonitorada;
import br.edu.fatecpg.backend_pulseira.repository.PessoaMonitoradaRepository;
import br.edu.fatecpg.backend_pulseira.security.ContaAutenticada;
import br.edu.fatecpg.backend_pulseira.service.AcessoService;
import br.edu.fatecpg.backend_pulseira.service.CuidadorService;
import br.edu.fatecpg.backend_pulseira.service.PessoaMonitoradaService;
import br.edu.fatecpg.backend_pulseira.service.VinculoCuidadorService;

import lombok.RequiredArgsConstructor;

/** Vínculo entre o responsável autenticado e uma pessoa monitorada. Quem inicia é sempre o responsável. */
@RestController
@RequestMapping("/api/vinculos")
@RequiredArgsConstructor
public class VinculoController {

    private final VinculoCuidadorService vinculoService;
    private final PessoaMonitoradaService pessoaService;
    private final PessoaMonitoradaRepository pessoaRepository;
    private final AcessoService acessoService;
    private final CuidadorService cuidadorService;

    /** Passo "Confirmar vínculo": usa o código que a pessoa compartilhou. */
    @PostMapping
    public PessoaResumoDTO vincular(
            @AuthenticationPrincipal ContaAutenticada conta,
            @RequestBody VinculoCodigoRequestDTO requisicao) {
        acessoService.exigirPerfil(conta, PerfilConta.RESPONSAVEL);
        PessoaMonitorada pessoa = pessoaService.buscarPorCodigo(requisicao.codigoVinculo());
        vinculoService.vincular(conta.getId(), pessoa.getId(), NivelAcesso.LEITURA);
        return PessoaResumoDTO.de(pessoa, cuidadorService.buscarContaDaPessoa(pessoa.getId()));
    }

    /** Pessoas que o responsável acompanha hoje (pode ser mais de uma). */
    @GetMapping("/meus")
    public List<PessoaResumoDTO> meus(@AuthenticationPrincipal ContaAutenticada conta) {
        acessoService.exigirPerfil(conta, PerfilConta.RESPONSAVEL);
        return vinculoService.listarAtivosDoCuidador(conta.getId()).stream()
                .map(vinculo -> pessoaRepository.findById(vinculo.getPessoaMonitoradaId()))
                .flatMap(Optional::stream)
                .map(pessoa -> PessoaResumoDTO.de(pessoa, cuidadorService.buscarContaDaPessoa(pessoa.getId())))
                .toList();
    }

    /** O responsável deixa de acompanhar a pessoa. */
    @DeleteMapping("/{pessoaId}")
    public ResponseEntity<Void> desvincular(
            @AuthenticationPrincipal ContaAutenticada conta,
            @PathVariable String pessoaId) {
        acessoService.exigirPerfil(conta, PerfilConta.RESPONSAVEL);
        vinculoService.desvincular(conta.getId(), pessoaId);
        return ResponseEntity.noContent().build();
    }
}
