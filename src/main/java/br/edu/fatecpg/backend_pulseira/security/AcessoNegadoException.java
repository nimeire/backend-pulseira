package br.edu.fatecpg.backend_pulseira.security;

/** A conta está autenticada, mas não pode acessar o recurso pedido (HTTP 403). */
public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
