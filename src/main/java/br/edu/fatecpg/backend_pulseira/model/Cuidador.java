package br.edu.fatecpg.backend_pulseira.model;

import java.time.Instant;
import java.time.LocalDate;

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

    /** Somente dígitos. Índice sparse: contas antigas, sem CPF, não colidem. */
    @Indexed(unique = true, sparse = true)
    private String cpf;

    private LocalDate dataNascimento;

    /** Ausente em contas antigas; nesse caso a conta é tratada como RESPONSAVEL. */
    private PerfilConta perfil;

    /** Preenchido apenas em contas PACIENTE: a pessoa monitorada que a conta representa. */
    private String pessoaMonitoradaId;

    /** Foto de perfil como data URL (JPEG/PNG/WebP em Base64), já reduzida pelo site. */
    private String fotoUrl;

    private Endereco endereco;
    private Boolean ativo;
    private Instant criadoEm;
}
