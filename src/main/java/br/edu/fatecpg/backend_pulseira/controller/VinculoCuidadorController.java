package br.edu.fatecpg.backend_pulseira.controller;

import br.edu.fatecpg.backend_pulseira.dto.VinculoCuidadorRequestDTO;
import br.edu.fatecpg.backend_pulseira.dto.VinculoCuidadorResponseDTO;
import br.edu.fatecpg.backend_pulseira.model.VinculoCuidador;
import br.edu.fatecpg.backend_pulseira.service.VinculoCuidadorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vinculos")
public class VinculoCuidadorController {

    private final VinculoCuidadorService vinculoService;

    public VinculoCuidadorController(VinculoCuidadorService vinculoService) {
        this.vinculoService = vinculoService;
    }

    @PostMapping
    public ResponseEntity<VinculoCuidadorResponseDTO> vincular(@Valid @RequestBody VinculoCuidadorRequestDTO dto) {
        VinculoCuidador vinculo = vinculoService.vincular(
                dto.cuidadorId(),
                dto.pessoaMonitoradaId(),
                dto.nivelAcesso()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(VinculoCuidadorResponseDTO.fromEntity(vinculo));
    }
}