package br.edu.fatecpg.backend_pulseira.dto;

import java.time.LocalDate;

import br.edu.fatecpg.backend_pulseira.model.PerfilConta;

/** Corpo do cadastro público. {@code perfil} é RESPONSAVEL (padrão) ou PACIENTE. */
public record CadastroContaRequestDTO(
        String nome,
        String cpf,
        LocalDate dataNascimento,
        String email,
        String telefone,
        String senha,
        PerfilConta perfil) {
}
