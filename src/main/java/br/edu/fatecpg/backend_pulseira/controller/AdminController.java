package br.edu.fatecpg.backend_pulseira.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecpg.backend_pulseira.dto.PulseiraAdminDTO;
import br.edu.fatecpg.backend_pulseira.dto.StatusContaRequestDTO;
import br.edu.fatecpg.backend_pulseira.dto.UsuarioAdminDTO;
import br.edu.fatecpg.backend_pulseira.service.AdminService;

import lombok.RequiredArgsConstructor;

/** Tela de administração. Protegido por {@code ROLE_ADMIN} no SecurityConfig. */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/usuarios")
    public List<UsuarioAdminDTO> listarUsuarios() {
        return adminService.listarUsuarios();
    }

    @PutMapping("/usuarios/{contaId}/status")
    public UsuarioAdminDTO alterarStatus(
            @PathVariable String contaId,
            @RequestBody StatusContaRequestDTO requisicao) {
        if (requisicao.ativo() == null) {
            throw new IllegalArgumentException("Informe se a conta deve ficar ativa.");
        }
        return adminService.alterarStatus(contaId, requisicao.ativo());
    }

    @DeleteMapping("/usuarios/{contaId}")
    public ResponseEntity<Void> excluir(@PathVariable String contaId) {
        adminService.excluir(contaId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pulseiras")
    public List<PulseiraAdminDTO> listarPulseiras() {
        return adminService.listarPulseiras();
    }
}
