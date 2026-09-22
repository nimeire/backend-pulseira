package br.edu.fatecpg.backend_pulseira.dto;

import br.edu.fatecpg.backend_pulseira.model.NivelAcesso;
import jakarta.validation.constraints.NotBlank;

public record VinculoCuidadorRequestDTO(
        @NotBlank(message = "O ID do cuidador é obrigatório.")
        String cuidadorId,
        @NotBlank(message = "O ID da pessoa monitorada é obrigatório.")
        String pessoaMonitoradaId,
        NivelAcesso nivelAcesso
) {}