package br.edu.fatecpg.backend_pulseira.repository;

import br.edu.fatecpg.backend_pulseira.model.Cuidador;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuidadorRepository extends MongoRepository<Cuidador, String> {
    Optional<Cuidador> findByEmail(String email);
    boolean existsByEmail(String email);
}