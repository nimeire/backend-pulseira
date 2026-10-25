package br.edu.fatecpg.backend_pulseira.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Registro de uma transmissão feita por uma pulseira.
 *
 * <p>O documento mantém, no mesmo instante de coleta, a posição GPS, os dados
 * do MPU6050 e a indicação de queda. Assim, o histórico pode ser recuperado
 * sem precisar combinar coleções distintas.</p>
 */
@Document(collection = "telemetrias")
@CompoundIndexes({
        @CompoundIndex(
                name = "dispositivo_registrado_em_idx",
                def = "{'dispositivoId': 1, 'registradoEm': -1}"
        ),
        @CompoundIndex(
                name = "pessoa_registrado_em_idx",
                def = "{'pessoaMonitoradaId': 1, 'registradoEm': -1}"
        )
})
@Getter
@Setter
@NoArgsConstructor
public class Telemetria {

    @Id
    private String id;

    private String dispositivoId;
    private String pulseiraId;
    private String pessoaMonitoradaId;
    private Instant registradoEm;

    private Double latitude;
    private Double longitude;
    private Double altitudeMetros;
    private Double precisaoMetros;

    private Double aceleracaoX;
    private Double aceleracaoY;
    private Double aceleracaoZ;

    private Double rotacaoX;
    private Double rotacaoY;
    private Double rotacaoZ;

    private Boolean quedaDetectada;
}
