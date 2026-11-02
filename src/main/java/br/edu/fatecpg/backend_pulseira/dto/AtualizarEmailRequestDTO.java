package br.edu.fatecpg.backend_pulseira.dto;

/** Trocar o e-mail exige a senha atual. */
public record AtualizarEmailRequestDTO(String email, String senhaAtual) {
}
