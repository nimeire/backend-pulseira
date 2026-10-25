package br.edu.fatecpg.backend_pulseira.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Endereço residencial da pessoa monitorada. É mantido como subdocumento para
 * que seja recuperado junto do seu cadastro, por exemplo em uma emergência.
 */
@Getter
@Setter
@NoArgsConstructor
public class Endereco {

    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
}
