package br.edu.fatecpg.backend_pulseira.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecpg.backend_pulseira.dto.AtualizarEmailRequestDTO;
import br.edu.fatecpg.backend_pulseira.dto.AtualizarFotoRequestDTO;
import br.edu.fatecpg.backend_pulseira.dto.AtualizarSenhaRequestDTO;
import br.edu.fatecpg.backend_pulseira.dto.AtualizarTelefoneRequestDTO;
import br.edu.fatecpg.backend_pulseira.dto.ContaResponseDTO;
import br.edu.fatecpg.backend_pulseira.dto.MudarPerfilRequestDTO;
import br.edu.fatecpg.backend_pulseira.security.ContaAutenticada;
import br.edu.fatecpg.backend_pulseira.service.ContaService;

import lombok.RequiredArgsConstructor;

/**
 * Configurações da própria conta. Quem muda é sempre a conta autenticada. Ao
 * trocar e-mail ou senha, o site precisa passar a enviar as novas credenciais.
 */
@RestController
@RequestMapping("/api/conta")
@RequiredArgsConstructor
public class ContaController {

    private final ContaService contaService;

    @PutMapping("/foto")
    public ContaResponseDTO atualizarFoto(
            @AuthenticationPrincipal ContaAutenticada conta,
            @RequestBody AtualizarFotoRequestDTO requisicao) {
        return ContaResponseDTO.de(contaService.atualizarFoto(conta.getId(), requisicao.fotoUrl()));
    }

    @PutMapping("/telefone")
    public ContaResponseDTO atualizarTelefone(
            @AuthenticationPrincipal ContaAutenticada conta,
            @RequestBody AtualizarTelefoneRequestDTO requisicao) {
        return ContaResponseDTO.de(contaService.atualizarTelefone(conta.getId(), requisicao.telefone()));
    }

    @PutMapping("/email")
    public ContaResponseDTO atualizarEmail(
            @AuthenticationPrincipal ContaAutenticada conta,
            @RequestBody AtualizarEmailRequestDTO requisicao) {
        return ContaResponseDTO.de(
                contaService.atualizarEmail(conta.getId(), requisicao.email(), requisicao.senhaAtual()));
    }

    @PutMapping("/senha")
    public ContaResponseDTO atualizarSenha(
            @AuthenticationPrincipal ContaAutenticada conta,
            @RequestBody AtualizarSenhaRequestDTO requisicao) {
        return ContaResponseDTO.de(
                contaService.atualizarSenha(conta.getId(), requisicao.senhaAtual(), requisicao.novaSenha()));
    }

    @PutMapping("/perfil")
    public ContaResponseDTO mudarPerfil(
            @AuthenticationPrincipal ContaAutenticada conta,
            @RequestBody MudarPerfilRequestDTO requisicao) {
        return ContaResponseDTO.de(contaService.mudarPerfil(conta.getId(), requisicao.perfil()));
    }
}
