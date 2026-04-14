package br.com.lata.velha.ordem_servico.application.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados do veículo")
public record VeiculoResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long proprietarioId,
        @Schema(example = "ABC-1D23") String placa,
        @Schema(example = "Fiat") String marca,
        @Schema(example = "Uno") String modelo,
        @Schema(example = "2020") Integer ano,
        @Schema(example = "Prata") String cor
) {}