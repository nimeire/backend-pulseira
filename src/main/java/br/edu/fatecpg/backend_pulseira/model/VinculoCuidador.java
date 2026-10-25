package br.edu.fatecpg.backend_pulseira.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Autoriza um cuidador a consultar os dados de uma pessoa monitorada.
 */
@Document(collection = "vinculos_cuidadores")
@CompoundIndex(
        name = "cuidador_pessoa_unico_idx",
        def = "{'cuidadorId': 1, 'pessoaMonitoradaId': 1}",
        unique = true
)
@Getter
@Setter
@NoArgsConstructor
public class VinculoCuidador {

    @Id
    private String id;

    private String cuidadorId;
    private String pessoaMonitoradaId;
    private NivelAcesso nivelAcesso;
    private Boolean ativo;
    private Instant criadoEm;
}
