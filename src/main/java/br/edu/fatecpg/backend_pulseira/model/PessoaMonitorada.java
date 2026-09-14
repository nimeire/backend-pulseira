package br.edu.fatecpg.backend_pulseira.model;

import java.time.Instant;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Pessoa que utiliza a pulseira. Ela não precisa, necessariamente, ter conta
 * de acesso ao aplicativo.
 */
@Document(collection = "pessoas_monitoradas")
public class PessoaMonitorada {

    @Id
    private String id;

    private String nome;
    private LocalDate dataNascimento;
    private String telefone;
    private Endereco endereco;
    private InformacoesMedicas informacoesMedicas;
    private Boolean ativa;
    private Instant criadoEm;

    public PessoaMonitorada() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }
    public InformacoesMedicas getInformacoesMedicas() { return informacoesMedicas; }
    public void setInformacoesMedicas(InformacoesMedicas informacoesMedicas) {
        this.informacoesMedicas = informacoesMedicas;
    }
    public Boolean getAtiva() { return ativa; }
    public void setAtiva(Boolean ativa) { this.ativa = ativa; }
    public Instant getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Instant criadoEm) { this.criadoEm = criadoEm; }
}
