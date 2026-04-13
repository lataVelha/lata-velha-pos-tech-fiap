package br.com.lata.velha.ordemDeServico.application.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PecaServicoResponse(

        @Schema(example = "1")
        Long id,

        @Schema(example = "pastilha")
        String nome,

        @Schema(example = "2")
        Integer quantidade

) {}