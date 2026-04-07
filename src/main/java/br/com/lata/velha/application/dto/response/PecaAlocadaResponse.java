package br.com.lata.velha.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PecaAlocadaResponse(
        @Schema(example = "10") Long id,
        @Schema(example = "5") Long pecaId,
        @Schema(example = "Pastilha de Freio") String pecaNome,
        @Schema(example = "2") Integer quantidadeAlocada,
        @Schema(example = "1") Long servicoOsId
) {}