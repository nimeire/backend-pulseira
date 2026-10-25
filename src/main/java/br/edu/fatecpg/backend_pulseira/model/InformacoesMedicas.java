package br.edu.fatecpg.backend_pulseira.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dados médicos mínimos úteis em um alerta. Devem ser exibidos somente para
 * cuidadores autorizados.
 */
@Getter
@Setter
@NoArgsConstructor
public class InformacoesMedicas {

    private String tipoSanguineo;
    private List<String> alergias = new ArrayList<>();
    private List<String> condicoes = new ArrayList<>();
    private List<String> medicamentosUsoContinuo = new ArrayList<>();
    private String observacoes;
}
