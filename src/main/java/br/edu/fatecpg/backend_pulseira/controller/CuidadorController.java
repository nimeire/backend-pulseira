package br.edu.fatecpg.backend_pulseira.controller;

import br.edu.fatecpg.backend_pulseira.dto.CuidadorRequestDTO;
import br.edu.fatecpg.backend_pulseira.dto.CuidadorResponseDTO;
import br.edu.fatecpg.backend_pulseira.model.Cuidador;
import br.edu.fatecpg.backend_pulseira.service.CuidadorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cuidadores")
public class CuidadorController {

    private final CuidadorService cuidadorService;

    public CuidadorController(CuidadorService cuidadorService) {
        this.cuidadorService = cuidadorService;
    }

    @PostMapping
    public ResponseEntity<CuidadorResponseDTO> cadastrar(@Valid @RequestBody CuidadorRequestDTO dto) {
        Cuidador cuidador = new Cuidador();
        cuidador.setNome(dto.nome());
        cuidador.setEmail(dto.email());
        cuidador.setSenhaHash(dto.senha()); // Futuramente pode aplicar BCryptPasswordEncoder

        Cuidador salvo = cuidadorService.cadastrar(cuidador);
        return ResponseEntity.status(HttpStatus.CREATED).body(CuidadorResponseDTO.fromEntity(salvo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuidadorResponseDTO> buscarAtivo(@PathVariable String id) {
        Cuidador cuidador = cuidadorService.buscarAtivo(id);
        return ResponseEntity.ok(CuidadorResponseDTO.fromEntity(cuidador));
    }
}