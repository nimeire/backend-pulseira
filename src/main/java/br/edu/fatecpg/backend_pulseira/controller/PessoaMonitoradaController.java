package br.edu.fatecpg.backend_pulseira.controller;

import br.edu.fatecpg.backend_pulseira.dto.PessoaMonitoradaRequestDTO;
import br.edu.fatecpg.backend_pulseira.dto.PessoaMonitoradaResponseDTO;
import br.edu.fatecpg.backend_pulseira.model.PessoaMonitorada;
import br.edu.fatecpg.backend_pulseira.service.PessoaMonitoradaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pessoas")
public class PessoaMonitoradaController {

    private final PessoaMonitoradaService pessoaService;

    public PessoaMonitoradaController(PessoaMonitoradaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @PostMapping
    public ResponseEntity<PessoaMonitoradaResponseDTO> cadastrar(@Valid @RequestBody PessoaMonitoradaRequestDTO dto) {
        PessoaMonitorada pessoa = new PessoaMonitorada();
        pessoa.setNome(dto.nome());
        pessoa.setDataNascimento(dto.dataNascimento());
        pessoa.setTelefone(dto.telefone());
        pessoa.setEndereco(dto.endereco());
        pessoa.setInformacoesMedicas(dto.informacoesMedicas());

        PessoaMonitorada salva = pessoaService.cadastrar(pessoa);
        return ResponseEntity.status(HttpStatus.CREATED).body(PessoaMonitoradaResponseDTO.fromEntity(salva));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaMonitoradaResponseDTO> buscarAtiva(@PathVariable String id) {
        PessoaMonitorada pessoa = pessoaService.buscarAtiva(id);
        return ResponseEntity.ok(PessoaMonitoradaResponseDTO.fromEntity(pessoa));
    }
}