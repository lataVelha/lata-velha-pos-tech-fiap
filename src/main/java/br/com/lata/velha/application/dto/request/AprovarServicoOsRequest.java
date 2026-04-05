package br.com.lata.velha.application.dto.request;

import br.com.lata.velha.domain.enuns.StatusServico;
import jakarta.validation.constraints.NotNull;

public record AprovarServicoOsRequest(
        @NotNull(message = "ID Serviço OS Os é obrigatório") Long idServicoOs,
        @NotNull(message = "Status Serviço OS Os é obrigatório") StatusServico statusServico
) {
}
