package br.edu.fatecpg.backend_pulseira.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.edu.fatecpg.backend_pulseira.model.Cuidador;

public interface CuidadorRepository extends MongoRepository<Cuidador, String> {
    Optional<Cuidador> findByEmail(String email);
    boolean existsByEmail(String email);
}
