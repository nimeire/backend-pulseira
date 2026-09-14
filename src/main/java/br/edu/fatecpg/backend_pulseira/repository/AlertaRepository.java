package br.edu.fatecpg.backend_pulseira.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.edu.fatecpg.backend_pulseira.model.Alerta;
import br.edu.fatecpg.backend_pulseira.model.StatusAlerta;

public interface AlertaRepository extends MongoRepository<Alerta, String> {
    List<Alerta> findByPessoaMonitoradaIdAndStatusOrderByGeradoEmDesc(
            String pessoaMonitoradaId,
            StatusAlerta status
    );

    List<Alerta> findByPessoaMonitoradaIdOrderByGeradoEmDesc(String pessoaMonitoradaId);
}
