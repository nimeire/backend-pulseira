package br.edu.fatecpg.backend_pulseira.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Autoriza um cuidador a consultar os dados de uma pessoa monitorada.
 */
@Document(collection = "vinculos_cuidadores")
@CompoundIndex(
        name = "cuidador_pessoa_unico_idx",
        def = "{'cuidadorId': 1, 'pessoaMonitoradaId': 1}",
        unique = true
)
public class VinculoCuidador {

    @Id
    private String id;

    private String cuidadorId;
    private String pessoaMonitoradaId;
    private NivelAcesso nivelAcesso;
    private Boolean ativo;
    private Instant criadoEm;

    public VinculoCuidador() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCuidadorId() { return cuidadorId; }
    public void setCuidadorId(String cuidadorId) { this.cuidadorId = cuidadorId; }
    public String getPessoaMonitoradaId() { return pessoaMonitoradaId; }
    public void setPessoaMonitoradaId(String pessoaMonitoradaId) { this.pessoaMonitoradaId = pessoaMonitoradaId; }
    public NivelAcesso getNivelAcesso() { return nivelAcesso; }
    public void setNivelAcesso(NivelAcesso nivelAcesso) { this.nivelAcesso = nivelAcesso; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    public Instant getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Instant criadoEm) { this.criadoEm = criadoEm; }
}
