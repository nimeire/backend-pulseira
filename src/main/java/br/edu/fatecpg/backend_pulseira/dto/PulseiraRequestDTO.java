package br.edu.fatecpg.backend_pulseira.dto;

import jakarta.validation.constraints.NotBlank;

public record PulseiraRequestDTO(
        @NotBlank(message = "O identificador do dispositivo é obrigatório.")
        String dispositivoId
) {}