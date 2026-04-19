package br.com.lata.velha.ordem_servico.application.dtos.response;

import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record AprovarOrdemServicoResponse(
        @Schema(description = "Id da Ordem de Serviço", example = "1") Long idOs,
        @Schema(description = "Status da Ordem de Serviço", example = "APROVADA") String status,
        @Schema(description = "Lista de serviços da OS") List<AprovarServicoOsResponse> aprovarServicoOsResponseList
) {
    public static AprovarOrdemServicoResponse from(OrdemServico domain) {
        List<AprovarServicoOsResponse> servicos = domain.getExecucaoServicos().stream()
                .map(s -> new AprovarServicoOsResponse(s.getId(), s.getStatus().name()))
                .toList();
        return new AprovarOrdemServicoResponse(domain.getId(), domain.getStatus().name(), servicos);
    }
}
