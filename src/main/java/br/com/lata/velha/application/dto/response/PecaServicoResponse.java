package br.com.lata.velha.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record PecaServicoResponse(

        @Schema(example = "10")
        Long alocacaoId,

        @Schema(example = "1")
        Long id,

        @Schema(example = "Pastilha de freio")
        String nome,

        @Schema(example = "4")
        Integer quantidadeSolicitada,

        @Schema(example = "2")
        Integer quantidadeReservada,

        @Schema(example = "2")
        Integer quantidadeEncomendada,

        @Schema(example = "PARCIAL")
        String status,

        @Schema(example = "2026-04-11T12:00:00")
        LocalDateTime atualizado

) {}