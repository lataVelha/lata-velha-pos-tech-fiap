package br.com.lata.velha.ordem_servico.api.dtos.ordem_servico;

import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.AprovarOrdemServicoUseCase;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.shared.api.validators.StatusAprovacaoValido;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

@Schema(name = "AprovarOrdemServicoRequest", description = "Dados para aprovação da Ordem de Serviço")
public record AprovarOrdemServicoRequest(
        @NotNull(message = "ID da OS é obrigatório")
        @Schema(description = "Id da ordem de serviço", example = "1")
        Long idOs,

        @NotEmpty(message = "Lista de serviços não pode ser vazia")
        @Schema(description = "Serviços a aprovar ou reprovar")
        List<@Valid Servico> servicos
) {
        public AprovarOrdemServicoUseCase.Input toInput(UserId userId) {
                List<AprovarOrdemServicoUseCase.Input.Servicos> servicosInput = servicos.stream()
                        .map(s -> new AprovarOrdemServicoUseCase.Input.Servicos(s.servicoOsId(), s.status()))
                        .toList();
                return new AprovarOrdemServicoUseCase.Input(idOs, userId, servicosInput);
        }

        @StatusAprovacaoValido
        public record Servico(
                @NotNull(message = "ID do serviço OS é obrigatório")
                @Schema(description = "Id da execução de serviço", example = "10")
                Long servicoOsId,

                @NotNull(message = "Status do serviço é obrigatório")
                @Schema(description = "Novo status: APROVADO ou RECUSADO")
                StatusExecucaoServico status
        ) {}
}
