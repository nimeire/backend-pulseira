package br.edu.fatecpg.backend_pulseira.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Cadastro da pulseira física e seu vínculo atual com a pessoa monitorada.
 */
@Document(collection = "pulseiras")
public class Pulseira {

    @Id
    private String id;

    @Indexed(unique = true)
    private String dispositivoId;

    private String pessoaMonitoradaId;
    private StatusPulseira status;
    private Instant vinculadaEm;
    private Instant atualizadaEm;

    public Pulseira() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDispositivoId() { return dispositivoId; }
    public void setDispositivoId(String dispositivoId) { this.dispositivoId = dispositivoId; }
    public String getPessoaMonitoradaId() { return pessoaMonitoradaId; }
    public void setPessoaMonitoradaId(String pessoaMonitoradaId) { this.pessoaMonitoradaId = pessoaMonitoradaId; }
    public StatusPulseira getStatus() { return status; }
    public void setStatus(StatusPulseira status) { this.status = status; }
    public Instant getVinculadaEm() { return vinculadaEm; }
    public void setVinculadaEm(Instant vinculadaEm) { this.vinculadaEm = vinculadaEm; }
    public Instant getAtualizadaEm() { return atualizadaEm; }
    public void setAtualizadaEm(Instant atualizadaEm) { this.atualizadaEm = atualizadaEm; }
}
