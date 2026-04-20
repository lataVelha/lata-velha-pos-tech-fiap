package br.com.lata.velha.ordem_servico.application.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "TotaisOrdemServicoResponse")
public record TotaisOrdemServicoResponse(

        @Schema(description = "Total da mão de obra dos serviços aprovados", example = "300.00")
        BigDecimal maoDeObraAprovada,

        @Schema(description = "Total das peças dos serviços aprovados", example = "80.00")
        BigDecimal pecasAprovadas,

        @Schema(description = "Total aprovado (mão de obra + peças)", example = "380.00")
        BigDecimal totalAprovado,

        @Schema(description = "Total da mão de obra dos serviços recusados", example = "150.00")
        BigDecimal totalRecusado

) {}
