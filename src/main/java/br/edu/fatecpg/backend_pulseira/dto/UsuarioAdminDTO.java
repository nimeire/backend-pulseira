package br.edu.fatecpg.backend_pulseira.dto;

import br.edu.fatecpg.backend_pulseira.model.Cuidador;
import br.edu.fatecpg.backend_pulseira.model.PerfilConta;

/** Linha da lista de usuários do administrador. */
public record UsuarioAdminDTO(
        String id,
        String nome,
        PerfilConta perfil,
        String cpf,
        String email,
        boolean ativo) {

    public static UsuarioAdminDTO de(Cuidador conta) {
        PerfilConta perfil = conta.getPerfil() == null ? PerfilConta.RESPONSAVEL : conta.getPerfil();
        return new UsuarioAdminDTO(
                conta.getId(),
                conta.getNome(),
                perfil,
                conta.getCpf(),
                conta.getEmail(),
                Boolean.TRUE.equals(conta.getAtivo()));
    }
}
