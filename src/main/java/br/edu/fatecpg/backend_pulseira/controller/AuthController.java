package br.edu.fatecpg.backend_pulseira.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecpg.backend_pulseira.dto.CadastroContaRequestDTO;
import br.edu.fatecpg.backend_pulseira.dto.ContaResponseDTO;
import br.edu.fatecpg.backend_pulseira.model.Cuidador;
import br.edu.fatecpg.backend_pulseira.security.ContaAutenticada;
import br.edu.fatecpg.backend_pulseira.service.ContaService;
import br.edu.fatecpg.backend_pulseira.service.CuidadorService;

import lombok.RequiredArgsConstructor;

/**
 * Cadastro e identificação da conta. Não há "login" separado: o site envia o
 * e-mail e a senha em cada chamada (HTTP Basic) e usa {@code GET /me} para
 * validar as credenciais e descobrir o tipo de conta.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ContaService contaService;
    private final CuidadorService cuidadorService;

    @PostMapping("/cadastro")
    public ResponseEntity<ContaResponseDTO> cadastrar(@RequestBody CadastroContaRequestDTO requisicao) {
        Cuidador conta = contaService.cadastrar(requisicao);
        return ResponseEntity.created(URI.create("/api/auth/me")).body(ContaResponseDTO.de(conta));
    }

    @GetMapping("/me")
    public ContaResponseDTO me(@AuthenticationPrincipal ContaAutenticada conta) {
        return ContaResponseDTO.de(cuidadorService.buscarAtivo(conta.getId()));
    }
}
