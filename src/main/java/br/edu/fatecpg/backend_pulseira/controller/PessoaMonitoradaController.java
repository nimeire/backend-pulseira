package br.edu.fatecpg.backend_pulseira.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fatecpg.backend_pulseira.model.PessoaMonitorada;
import br.edu.fatecpg.backend_pulseira.service.PessoaMonitoradaService;

/** Cadastro inicial da pessoa que utilizará a pulseira. */
@RestController
@RequestMapping("/api/pessoas-monitoradas")
public class PessoaMonitoradaController {

    private final PessoaMonitoradaService pessoaService;

    public PessoaMonitoradaController(PessoaMonitoradaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @PostMapping
    public ResponseEntity<PessoaMonitorada> cadastrar(@RequestBody PessoaMonitorada pessoa) {
        PessoaMonitorada salva = pessoaService.cadastrar(pessoa);
        return ResponseEntity.created(URI.create("/api/pessoas-monitoradas/" + salva.getId())).body(salva);
    }
}
