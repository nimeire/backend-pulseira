package br.edu.fatecpg.backend_pulseira.dto;

import br.edu.fatecpg.backend_pulseira.model.StatusPulseira;

/** Linha da lista de pulseiras do administrador. */
public record PulseiraAdminDTO(
        String id,
        String dispositivoId,
        StatusPulseira status,
        String pacienteNome) {
}
