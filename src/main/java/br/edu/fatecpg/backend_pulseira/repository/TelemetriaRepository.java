package br.edu.fatecpg.backend_pulseira.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.edu.fatecpg.backend_pulseira.model.Telemetria;

/**
 * Contrato de persistência das telemetrias recebidas das pulseiras.
 */
public interface TelemetriaRepository extends MongoRepository<Telemetria, String> {

    Optional<Telemetria> findFirstByDispositivoIdOrderByRegistradoEmDesc(String dispositivoId);

    Optional<Telemetria> findFirstByPessoaMonitoradaIdOrderByRegistradoEmDesc(String pessoaMonitoradaId);

    List<Telemetria> findByDispositivoIdAndRegistradoEmBetweenOrderByRegistradoEmDesc(
            String dispositivoId,
            Instant inicio,
            Instant fim
    );

    List<Telemetria> findByDispositivoIdAndQuedaDetectadaTrueOrderByRegistradoEmDesc(String dispositivoId);

    List<Telemetria> findByPessoaMonitoradaIdAndRegistradoEmBetweenOrderByRegistradoEmDesc(
            String pessoaMonitoradaId,
            Instant inicio,
            Instant fim
    );

    List<Telemetria> findByPessoaMonitoradaIdAndQuedaDetectadaTrueOrderByRegistradoEmDesc(
            String pessoaMonitoradaId
    );
}
