package br.edu.fatecpg.backend_pulseira.dto;

import java.util.List;

import org.springframework.data.domain.Page;

/** Uma página de resultados. {@code pagina} começa em 1. */
public record PaginaDTO<T>(
        List<T> itens,
        int pagina,
        int tamanho,
        long totalItens,
        int totalPaginas) {

    public static <T> PaginaDTO<T> de(Page<T> pagina) {
        return new PaginaDTO<>(
                pagina.getContent(),
                pagina.getNumber() + 1,
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getTotalPages());
    }
}
