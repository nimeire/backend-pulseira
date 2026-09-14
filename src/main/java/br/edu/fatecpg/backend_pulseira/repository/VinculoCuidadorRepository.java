package br.edu.fatecpg.backend_pulseira.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.edu.fatecpg.backend_pulseira.model.VinculoCuidador;

public interface VinculoCuidadorRepository extends MongoRepository<VinculoCuidador, String> {
    List<VinculoCuidador> findByCuidadorIdAndAtivoTrue(String cuidadorId);
    boolean existsByCuidadorIdAndPessoaMonitoradaIdAndAtivoTrue(String cuidadorId, String pessoaMonitoradaId);
}
