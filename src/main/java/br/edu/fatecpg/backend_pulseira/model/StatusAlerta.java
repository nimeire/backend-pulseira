package br.edu.fatecpg.backend_pulseira.model;

/**
 * Ciclo de vida de um alerta enviado para a dashboard do cuidador.
 */
public enum StatusAlerta {
    PENDENTE,
    RECONHECIDO,
    RESOLVIDO,
    FALSO_POSITIVO
}
