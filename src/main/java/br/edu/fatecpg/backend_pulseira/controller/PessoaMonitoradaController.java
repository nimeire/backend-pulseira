package br.edu.fatecpg.backend_pulseira.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecpg.backend_pulseira.dto.PessoaMonitoradaDTO;
import br.edu.fatecpg.backend_pulseira.dto.PessoaResumoDTO;
import br.edu.fatecpg.backend_pulseira.dto.ResponsavelDTO;
import br.edu.fatecpg.backend_pulseira.model.Endereco;
import br.edu.fatecpg.backend_pulseira.model.InformacoesMedicas;
import br.edu.fatecpg.backend_pulseira.model.PerfilConta;
import br.edu.fatecpg.backend_pulseira.model.PessoaMonitorada;
import br.edu.fatecpg.backend_pulseira.model.Pulseira;
import br.edu.fatecpg.backend_pulseira.security.ContaAutenticada;
import br.edu.fatecpg.backend_pulseira.service.AcessoService;
import br.edu.fatecpg.backend_pulseira.service.CuidadorService;
import br.edu.fatecpg.backend_pulseira.service.PessoaMonitoradaService;
import br.edu.fatecpg.backend_pulseira.service.PulseiraService;
import br.edu.fatecpg.backend_pulseira.service.VinculoCuidadorService;

import lombok.RequiredArgsConstructor;

/** Cadastro da pessoa que utiliza a pulseira e consultas sobre ela. */
@RestController
@RequestMapping("/api/pessoas-monitoradas")
@RequiredArgsConstructor
public class PessoaMonitoradaController {

    private final PessoaMonitoradaService pessoaService;
    private final AcessoService acessoService;
    private final VinculoCuidadorService vinculoService;
    private final CuidadorService cuidadorService;
    private final PulseiraService pulseiraService;

    @PostMapping
    public ResponseEntity<PessoaMonitorada> cadastrar(@RequestBody PessoaMonitorada pessoa) {
        PessoaMonitorada salva = pessoaService.cadastrar(pessoa);
        return ResponseEntity.created(URI.create("/api/pessoas-monitoradas/" + salva.getId())).body(salva);
    }

    @GetMapping("/{pessoaId}")
    public PessoaMonitoradaDTO buscar(
            @AuthenticationPrincipal ContaAutenticada conta,
            @PathVariable String pessoaId) {
        acessoService.exigirLeitura(conta, pessoaId);
        return PessoaMonitoradaDTO.de(
                pessoaService.buscarAtiva(pessoaId),
                acessoService.ehDona(conta, pessoaId),
                cuidadorService.buscarContaDaPessoa(pessoaId));
    }

    @PutMapping("/{pessoaId}/informacoes-medicas")
    public PessoaMonitoradaDTO atualizarInformacoesMedicas(
            @AuthenticationPrincipal ContaAutenticada conta,
            @PathVariable String pessoaId,
            @RequestBody InformacoesMedicas informacoes) {
        acessoService.exigirDona(conta, pessoaId);
        PessoaMonitorada atualizada = pessoaService.atualizarInformacoesMedicas(pessoaId, informacoes);
        return PessoaMonitoradaDTO.de(
                atualizada,
                acessoService.ehDona(conta, pessoaId),
                cuidadorService.buscarContaDaPessoa(pessoaId));
    }

    @PutMapping("/{pessoaId}/endereco")
    public PessoaMonitoradaDTO atualizarEndereco(
            @AuthenticationPrincipal ContaAutenticada conta,
            @PathVariable String pessoaId,
            @RequestBody Endereco endereco) {
        acessoService.exigirDona(conta, pessoaId);
        PessoaMonitorada atualizada = pessoaService.atualizarEndereco(pessoaId, endereco);
        return PessoaMonitoradaDTO.de(
                atualizada,
                acessoService.ehDona(conta, pessoaId),
                cuidadorService.buscarContaDaPessoa(pessoaId));
    }

    /** Passo "Buscar paciente" do responsável: mostra o resumo antes de confirmar o vínculo. */
    @GetMapping("/por-codigo/{codigo}")
    public PessoaResumoDTO buscarPorCodigo(
            @AuthenticationPrincipal ContaAutenticada conta,
            @PathVariable String codigo) {
        acessoService.exigirPerfil(conta, PerfilConta.RESPONSAVEL);
        PessoaMonitorada pessoa = pessoaService.buscarPorCodigo(codigo);
        return PessoaResumoDTO.de(pessoa, cuidadorService.buscarContaDaPessoa(pessoa.getId()));
    }

    @GetMapping("/{pessoaId}/responsaveis")
    public List<ResponsavelDTO> listarResponsaveis(
            @AuthenticationPrincipal ContaAutenticada conta,
            @PathVariable String pessoaId) {
        acessoService.exigirLeitura(conta, pessoaId);
        return vinculoService.listarAtivosDaPessoa(pessoaId).stream()
                .map(vinculo -> cuidadorService.buscarPorId(vinculo.getCuidadorId()))
                .flatMap(java.util.Optional::stream)
                .map(ResponsavelDTO::de)
                .toList();
    }

    /** A própria pessoa desvincula um responsável. */
    @DeleteMapping("/{pessoaId}/responsaveis/{cuidadorId}")
    public ResponseEntity<Void> desvincularResponsavel(
            @AuthenticationPrincipal ContaAutenticada conta,
            @PathVariable String pessoaId,
            @PathVariable String cuidadorId) {
        acessoService.exigirDona(conta, pessoaId);
        vinculoService.desvincular(cuidadorId, pessoaId);
        return ResponseEntity.noContent().build();
    }

    /** Pulseira atual da pessoa (com o último sinal); 204 se ainda não houver. */
    @GetMapping("/{pessoaId}/pulseira")
    public ResponseEntity<Pulseira> pulseira(
            @AuthenticationPrincipal ContaAutenticada conta,
            @PathVariable String pessoaId) {
        acessoService.exigirLeitura(conta, pessoaId);
        return pulseiraService.buscarPorPessoa(pessoaId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
