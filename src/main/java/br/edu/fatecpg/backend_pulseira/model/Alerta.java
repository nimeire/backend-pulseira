package br.edu.fatecpg.backend_pulseira.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Registro de um evento que exige atenção do cuidador. A telemetria mantém a
 * leitura bruta; este documento acompanha o atendimento do alerta.
 */
@Document(collection = "alertas")
@CompoundIndex(
        name = "pessoa_status_gerado_em_idx",
        def = "{'pessoaMonitoradaId': 1, 'status': 1, 'geradoEm': -1}"
)
public class Alerta {

    @Id
    private String id;

    private TipoAlerta tipo;
    private StatusAlerta status;
    private String pessoaMonitoradaId;
    private String pulseiraId;
    private String telemetriaId;
    private Instant geradoEm;
    private Instant reconhecidoEm;
    private String cuidadorReconheceuId;
    private Instant resolvidoEm;
    private String observacao;

    public Alerta() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public TipoAlerta getTipo() { return tipo; }
    public void setTipo(TipoAlerta tipo) { this.tipo = tipo; }
    public StatusAlerta getStatus() { return status; }
    public void setStatus(StatusAlerta status) { this.status = status; }
    public String getPessoaMonitoradaId() { return pessoaMonitoradaId; }
    public void setPessoaMonitoradaId(String pessoaMonitoradaId) { this.pessoaMonitoradaId = pessoaMonitoradaId; }
    public String getPulseiraId() { return pulseiraId; }
    public void setPulseiraId(String pulseiraId) { this.pulseiraId = pulseiraId; }
    public String getTelemetriaId() { return telemetriaId; }
    public void setTelemetriaId(String telemetriaId) { this.telemetriaId = telemetriaId; }
    public Instant getGeradoEm() { return geradoEm; }
    public void setGeradoEm(Instant geradoEm) { this.geradoEm = geradoEm; }
    public Instant getReconhecidoEm() { return reconhecidoEm; }
    public void setReconhecidoEm(Instant reconhecidoEm) { this.reconhecidoEm = reconhecidoEm; }
    public String getCuidadorReconheceuId() { return cuidadorReconheceuId; }
    public void setCuidadorReconheceuId(String cuidadorReconheceuId) {
        this.cuidadorReconheceuId = cuidadorReconheceuId;
    }
    public Instant getResolvidoEm() { return resolvidoEm; }
    public void setResolvidoEm(Instant resolvidoEm) { this.resolvidoEm = resolvidoEm; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
