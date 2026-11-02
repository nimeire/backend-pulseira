package br.edu.fatecpg.backend_pulseira.dto;

/** Corpo das ações do cuidador sobre um alerta (reconhecer, resolver, falso positivo). */
public record AlertaAcaoRequestDTO(String cuidadorId, String observacao) {
}
