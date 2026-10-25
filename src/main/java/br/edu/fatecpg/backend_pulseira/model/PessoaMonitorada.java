package br.edu.fatecpg.backend_pulseira.model;

import java.time.Instant;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Pessoa que utiliza a pulseira. Ela não precisa, necessariamente, ter conta
 * de acesso ao aplicativo.
 */
@Document(collection = "pessoas_monitoradas")
@Getter
@Setter
@NoArgsConstructor
public class PessoaMonitorada {

    @Id
    private String id;

    private String nome;
    private LocalDate dataNascimento;
    private String telefone;
    private Endereco endereco;
    private InformacoesMedicas informacoesMedicas;
    private Boolean ativa;
    private Instant criadoEm;
}
