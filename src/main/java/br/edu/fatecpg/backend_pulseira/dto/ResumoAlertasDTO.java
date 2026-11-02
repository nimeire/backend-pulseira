package br.edu.fatecpg.backend_pulseira.dto;

import br.edu.fatecpg.backend_pulseira.model.Alerta;

/** Visão rápida para o painel: quantos alertas esperam atendimento e qual foi o mais recente. */
public record ResumoAlertasDTO(long pendentes, Alerta ultimoAlerta) {
}
