package br.edu.fatecpg.backend_pulseira.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cadastro da pulseira física e seu vínculo atual com a pessoa monitorada.
 */
@Document(collection = "pulseiras")
@Getter
@Setter
@NoArgsConstructor
public class Pulseira {

    @Id
    private String id;

    @Indexed(unique = true)
    private String dispositivoId;

    private String pessoaMonitoradaId;
    private StatusPulseira status;
    private Instant vinculadaEm;
    private Instant atualizadaEm;
}
