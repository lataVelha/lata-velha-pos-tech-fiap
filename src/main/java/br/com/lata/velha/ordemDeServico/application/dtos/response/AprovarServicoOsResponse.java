package br.com.lata.velha.ordemDeServico.application.dtos.response;


import io.swagger.v3.oas.annotations.media.Schema;

public record AprovarServicoOsResponse(
        @Schema(example = "Id Serviço Os")Long idServicoOs,
        @Schema(example = "Status Serviço Os")String statusServico) {
}
