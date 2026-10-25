package br.edu.fatecpg.backend_pulseira.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Registro de um evento que exige atenção do cuidador. A telemetria mantém a
 * leitura bruta; este documento acompanha o atendimento do alerta.
 */
@Document(collection = "alertas")
@CompoundIndex(
        name = "pessoa_status_gerado_em_idx",
        def = "{'pessoaMonitoradaId': 1, 'status': 1, 'geradoEm': -1}"
)
@Getter
@Setter
@NoArgsConstructor
public class Alerta {

    @Id
    private String id;

    private TipoAlerta tipo;
    private StatusAlerta status;
    private String pessoaMonitoradaId;
    private String pulseiraId;
    private String telemetriaId;
    private Instant geradoEm;
    private Instant reconhecidoEm;
    private String cuidadorReconheceuId;
    private Instant resolvidoEm;
    private String observacao;
}
