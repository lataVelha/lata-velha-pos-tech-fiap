package br.com.lata.velha.ordemDeServico.application.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record AprovarOrdemServicoResponse(
        @Schema(example = "Id  Os") Long idOs,
        @Schema(example = "Status Os")String status,
        @Schema(example = "Lista Serviços Os") List<AprovarServicoOsResponse> aprovarServicoOsResponseList
) {
}
