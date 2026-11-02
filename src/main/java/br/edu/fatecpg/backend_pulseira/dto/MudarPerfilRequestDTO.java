package br.edu.fatecpg.backend_pulseira.dto;

import br.edu.fatecpg.backend_pulseira.model.PerfilConta;

/** RESPONSAVEL ou PACIENTE. */
public record MudarPerfilRequestDTO(PerfilConta perfil) {
}
