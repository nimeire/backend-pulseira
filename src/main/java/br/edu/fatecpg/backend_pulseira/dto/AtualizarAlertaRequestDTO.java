package br.edu.fatecpg.backend_pulseira.dto;

public record AtualizarAlertaRequestDTO(
        String cuidadorId,
        String observacao
) {}
