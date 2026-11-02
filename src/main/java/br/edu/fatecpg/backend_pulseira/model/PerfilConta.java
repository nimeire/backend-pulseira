package br.edu.fatecpg.backend_pulseira.model;

/**
 * Tipo de conta de acesso. O administrador não é criado pelo cadastro público:
 * ele é provisionado pela configuração do servidor.
 */
public enum PerfilConta {
    RESPONSAVEL,
    PACIENTE,
    ADMIN
}
