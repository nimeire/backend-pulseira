package br.edu.fatecpg.backend_pulseira.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CuidadorRequestDTO(
        @NotBlank(message = "O nome do cuidador é obrigatório.")
        String nome,
        @Email(message = "Informe um e-mail válido.")
        @NotBlank(message = "O e-mail é obrigatório.")
        String email,
        @NotBlank(message = "A palavra-passe é obrigatória.")
        String senha
) {}