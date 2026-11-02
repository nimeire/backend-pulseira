package br.edu.fatecpg.backend_pulseira.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import br.edu.fatecpg.backend_pulseira.model.Alerta;
import br.edu.fatecpg.backend_pulseira.model.StatusAlerta;

public interface AlertaRepository extends MongoRepository<Alerta, String> {
    List<Alerta> findByPessoaMonitoradaIdAndStatusOrderByGeradoEmDesc(
            String pessoaMonitoradaId,
            StatusAlerta status
    );

    List<Alerta> findByPessoaMonitoradaIdOrderByGeradoEmDesc(String pessoaMonitoradaId);

    Optional<Alerta> findFirstByPessoaMonitoradaIdOrderByGeradoEmDesc(String pessoaMonitoradaId);

    Page<Alerta> findByPessoaMonitoradaId(String pessoaMonitoradaId, Pageable paginacao);

    Page<Alerta> findByPessoaMonitoradaIdAndGeradoEmGreaterThanEqual(
            String pessoaMonitoradaId,
            Instant desde,
            Pageable paginacao
    );

    long countByPessoaMonitoradaIdAndStatus(String pessoaMonitoradaId, StatusAlerta status);
}
