package br.com.lata.velha.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Resposta paginada")
public record PaginatedResponse<T>(

        @Schema(description = "Conteúdo da página")
        List<T> content,

        @Schema(description = "Número da página (começa em 0)", example = "0")
        int page,

        @Schema(description = "Itens por página", example = "10")
        int size,

        @Schema(description = "Total de registros", example = "50")
        long totalElements,

        @Schema(description = "Total de páginas", example = "5")
        int totalPages
) {}