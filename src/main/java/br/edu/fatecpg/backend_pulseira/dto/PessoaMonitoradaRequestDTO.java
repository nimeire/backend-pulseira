package br.edu.fatecpg.backend_pulseira.dto;

import br.edu.fatecpg.backend_pulseira.model.Endereco;
import br.edu.fatecpg.backend_pulseira.model.InformacoesMedicas;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record PessoaMonitoradaRequestDTO(
        @NotBlank(message = "O nome é obrigatório")
        String nome,
        LocalDate dataNascimento,
        String telefone,
        Endereco endereco,
        InformacoesMedicas informacoesMedicas
) {}