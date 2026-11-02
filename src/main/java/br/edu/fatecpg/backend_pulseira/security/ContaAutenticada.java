package br.edu.fatecpg.backend_pulseira.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.edu.fatecpg.backend_pulseira.model.Cuidador;
import br.edu.fatecpg.backend_pulseira.model.PerfilConta;

/**
 * Conta autenticada pelo HTTP Basic. Os controllers usam o {@code id} daqui,
 * nunca um identificador enviado pelo cliente, para saber quem está agindo.
 */
public class ContaAutenticada implements UserDetails {

    private final String id;
    private final String email;
    private final String senhaHash;
    private final PerfilConta perfil;
    private final String pessoaMonitoradaId;
    private final boolean ativa;

    public ContaAutenticada(Cuidador conta) {
        this.id = conta.getId();
        this.email = conta.getEmail();
        this.senhaHash = conta.getSenhaHash();
        this.perfil = conta.getPerfil() == null ? PerfilConta.RESPONSAVEL : conta.getPerfil();
        this.pessoaMonitoradaId = conta.getPessoaMonitoradaId();
        this.ativa = Boolean.TRUE.equals(conta.getAtivo());
    }

    public String getId() {
        return id;
    }

    public PerfilConta getPerfil() {
        return perfil;
    }

    public String getPessoaMonitoradaId() {
        return pessoaMonitoradaId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + perfil.name()));
    }

    @Override
    public String getPassword() {
        return senhaHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return ativa;
    }
}
