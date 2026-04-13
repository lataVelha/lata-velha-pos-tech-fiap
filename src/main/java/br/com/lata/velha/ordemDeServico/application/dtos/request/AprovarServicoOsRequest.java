package br.com.lata.velha.ordemDeServico.application.dtos.request;

import br.com.lata.velha.ordemDeServico.domain.enums.StatusServico;
import jakarta.validation.constraints.NotNull;

public record AprovarServicoOsRequest(
        @NotNull(message = "ID Serviço OS Os é obrigatório") Long idServicoOs,
        @NotNull(message = "Status Serviço OS Os é obrigatório") StatusServico statusServico
) {
}
