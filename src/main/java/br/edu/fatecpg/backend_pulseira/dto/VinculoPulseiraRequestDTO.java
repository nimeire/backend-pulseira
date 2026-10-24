package br.edu.fatecpg.backend_pulseira.dto;

/** Corpo da requisição que associa uma pulseira já cadastrada a uma pessoa. */
public record VinculoPulseiraRequestDTO(String pessoaMonitoradaId) {
}
