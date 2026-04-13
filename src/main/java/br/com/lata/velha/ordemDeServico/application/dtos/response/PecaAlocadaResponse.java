package br.com.lata.velha.ordemDeServico.application.dtos.response;

import br.com.lata.velha.ordemDeServico.domain.enums.StatusPecaAlocada;
import io.swagger.v3.oas.annotations.media.Schema;

public record PecaAlocadaResponse(

        @Schema(example = "10")
        Long id,

        @Schema(example = "5")
        Long pecaId,

        @Schema(example = "Pastilha de Freio")
        String pecaNome,

        @Schema(example = "3")
        Integer quantidadeSolicitada,

        @Schema(example = "3")
        Integer quantidadeAlocada,

        @Schema(example = "1")
        Integer quantidadeReservada,

        @Schema(example = "2")
        Integer quantidadeEncomendada,

        @Schema(example = "PARCIAL")
        StatusPecaAlocada status,

        @Schema(example = "1")
        Long servicoOsId
) {}