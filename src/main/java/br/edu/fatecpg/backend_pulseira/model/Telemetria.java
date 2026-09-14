package br.edu.fatecpg.backend_pulseira.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Registro de uma transmissão feita por uma pulseira.
 *
 * <p>O documento mantém, no mesmo instante de coleta, a posição GPS, os dados
 * do MPU6050 e a indicação de queda. Assim, o histórico pode ser recuperado
 * sem precisar combinar coleções distintas.</p>
 */
@Document(collection = "telemetrias")
@CompoundIndexes({
        @CompoundIndex(
                name = "dispositivo_registrado_em_idx",
                def = "{'dispositivoId': 1, 'registradoEm': -1}"
        ),
        @CompoundIndex(
                name = "pessoa_registrado_em_idx",
                def = "{'pessoaMonitoradaId': 1, 'registradoEm': -1}"
        )
})
public class Telemetria {

    @Id
    private String id;

    private String dispositivoId;
    private String pulseiraId;
    private String pessoaMonitoradaId;
    private Instant registradoEm;

    private Double latitude;
    private Double longitude;
    private Double altitudeMetros;
    private Double precisaoMetros;

    private Double aceleracaoX;
    private Double aceleracaoY;
    private Double aceleracaoZ;

    private Double rotacaoX;
    private Double rotacaoY;
    private Double rotacaoZ;

    private Boolean quedaDetectada;

    public Telemetria() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDispositivoId() {
        return dispositivoId;
    }

    public void setDispositivoId(String dispositivoId) {
        this.dispositivoId = dispositivoId;
    }

    public String getPulseiraId() {
        return pulseiraId;
    }

    public void setPulseiraId(String pulseiraId) {
        this.pulseiraId = pulseiraId;
    }

    public String getPessoaMonitoradaId() {
        return pessoaMonitoradaId;
    }

    public void setPessoaMonitoradaId(String pessoaMonitoradaId) {
        this.pessoaMonitoradaId = pessoaMonitoradaId;
    }

    public Instant getRegistradoEm() {
        return registradoEm;
    }

    public void setRegistradoEm(Instant registradoEm) {
        this.registradoEm = registradoEm;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getAltitudeMetros() {
        return altitudeMetros;
    }

    public void setAltitudeMetros(Double altitudeMetros) {
        this.altitudeMetros = altitudeMetros;
    }

    public Double getPrecisaoMetros() {
        return precisaoMetros;
    }

    public void setPrecisaoMetros(Double precisaoMetros) {
        this.precisaoMetros = precisaoMetros;
    }

    public Double getAceleracaoX() {
        return aceleracaoX;
    }

    public void setAceleracaoX(Double aceleracaoX) {
        this.aceleracaoX = aceleracaoX;
    }

    public Double getAceleracaoY() {
        return aceleracaoY;
    }

    public void setAceleracaoY(Double aceleracaoY) {
        this.aceleracaoY = aceleracaoY;
    }

    public Double getAceleracaoZ() {
        return aceleracaoZ;
    }

    public void setAceleracaoZ(Double aceleracaoZ) {
        this.aceleracaoZ = aceleracaoZ;
    }

    public Double getRotacaoX() {
        return rotacaoX;
    }

    public void setRotacaoX(Double rotacaoX) {
        this.rotacaoX = rotacaoX;
    }

    public Double getRotacaoY() {
        return rotacaoY;
    }

    public void setRotacaoY(Double rotacaoY) {
        this.rotacaoY = rotacaoY;
    }

    public Double getRotacaoZ() {
        return rotacaoZ;
    }

    public void setRotacaoZ(Double rotacaoZ) {
        this.rotacaoZ = rotacaoZ;
    }

    public Boolean getQuedaDetectada() {
        return quedaDetectada;
    }

    public void setQuedaDetectada(Boolean quedaDetectada) {
        this.quedaDetectada = quedaDetectada;
    }
}
