package br.com.lata.velha.ordem_servico.application.dtos.response;


import io.swagger.v3.oas.annotations.media.Schema;

public record AprovarServicoOsResponse(
        @Schema(description = "Id do Serviço da OS", example = "1") Long idServicoOs,
        @Schema(description = "Status do Serviço da OS", example = "APROVADO") String statusServico) {
}
