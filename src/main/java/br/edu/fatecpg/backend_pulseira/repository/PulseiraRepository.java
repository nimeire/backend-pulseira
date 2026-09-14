package br.edu.fatecpg.backend_pulseira.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.edu.fatecpg.backend_pulseira.model.Pulseira;

public interface PulseiraRepository extends MongoRepository<Pulseira, String> {
    Optional<Pulseira> findByDispositivoId(String dispositivoId);
    List<Pulseira> findByPessoaMonitoradaId(String pessoaMonitoradaId);
}
