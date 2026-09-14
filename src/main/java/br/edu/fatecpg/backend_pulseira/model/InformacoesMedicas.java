package br.edu.fatecpg.backend_pulseira.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Dados médicos mínimos úteis em um alerta. Devem ser exibidos somente para
 * cuidadores autorizados.
 */
public class InformacoesMedicas {

    private String tipoSanguineo;
    private List<String> alergias = new ArrayList<>();
    private List<String> condicoes = new ArrayList<>();
    private List<String> medicamentosUsoContinuo = new ArrayList<>();
    private String observacoes;

    public InformacoesMedicas() {
    }

    public String getTipoSanguineo() { return tipoSanguineo; }
    public void setTipoSanguineo(String tipoSanguineo) { this.tipoSanguineo = tipoSanguineo; }
    public List<String> getAlergias() { return alergias; }
    public void setAlergias(List<String> alergias) { this.alergias = alergias; }
    public List<String> getCondicoes() { return condicoes; }
    public void setCondicoes(List<String> condicoes) { this.condicoes = condicoes; }
    public List<String> getMedicamentosUsoContinuo() { return medicamentosUsoContinuo; }
    public void setMedicamentosUsoContinuo(List<String> medicamentosUsoContinuo) {
        this.medicamentosUsoContinuo = medicamentosUsoContinuo;
    }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
