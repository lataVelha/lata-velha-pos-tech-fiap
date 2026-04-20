package br.com.lata.velha.ordem_servico.application.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "TotaisOrdemServicoResponse")
public record TotaisOrdemServicoResponse(

        @Schema(description = "Total dos serviços (mão de obra)", example = "150.00")
        BigDecimal servicos,

        @Schema(description = "Total das peças", example = "80.00")
        BigDecimal pecas,

        @Schema(description = "Total geral da OS", example = "230.00")
        BigDecimal total

) {}
