package br.edu.fatecpg.backend_pulseira.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.edu.fatecpg.backend_pulseira.model.VinculoCuidador;

public interface VinculoCuidadorRepository extends MongoRepository<VinculoCuidador, String> {
    List<VinculoCuidador> findByCuidadorIdAndAtivoTrue(String cuidadorId);
    boolean existsByCuidadorIdAndPessoaMonitoradaIdAndAtivoTrue(String cuidadorId, String pessoaMonitoradaId);
    Optional<VinculoCuidador> findByCuidadorIdAndPessoaMonitoradaId(String cuidadorId, String pessoaMonitoradaId);
    List<VinculoCuidador> findByPessoaMonitoradaIdAndAtivoTrue(String pessoaMonitoradaId);
    void deleteByCuidadorId(String cuidadorId);
}
