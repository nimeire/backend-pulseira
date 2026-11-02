package br.edu.fatecpg.backend_pulseira.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.edu.fatecpg.backend_pulseira.model.PessoaMonitorada;

public interface PessoaMonitoradaRepository extends MongoRepository<PessoaMonitorada, String> {
    List<PessoaMonitorada> findByAtivaTrue();
    Optional<PessoaMonitorada> findByCodigoVinculo(String codigoVinculo);
    boolean existsByCodigoVinculo(String codigoVinculo);
}
