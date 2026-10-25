package br.edu.fatecpg.backend_pulseira.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Conta que acessa a aplicação para acompanhar pessoas monitoradas.
 * A senha nunca é armazenada; somente o hash gerado pela camada de segurança.
 */
@Document(collection = "cuidadores")
@Getter
@Setter
@NoArgsConstructor
public class Cuidador {

    @Id
    private String id;

    private String nome;

    @Indexed(unique = true)
    private String email;

    private String senhaHash;
    private String telefone;
    private Endereco endereco;
    private Boolean ativo;
    private Instant criadoEm;
}
