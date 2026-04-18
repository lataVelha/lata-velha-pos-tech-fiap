package br.com.lata.velha.ordem_servico.application.dtos.request;

import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import jakarta.validation.constraints.NotNull;

public record AprovarServicoOsRequest(
        @NotNull(message = "ID Serviço OS Os é obrigatório") Long idServicoOs,
        @NotNull(message = "Status Serviço OS Os é obrigatório") StatusExecucaoServico statusExecucaoServico
) {
}
