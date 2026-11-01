package br.edu.fatecpg.backend_pulseira.dto;

public record LoginResponseDTO(
        String token,
        String tipo,
        String usuarioId,
        String nome,
        String email,
        String role
) {}