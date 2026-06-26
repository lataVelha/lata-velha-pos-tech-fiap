package br.com.lata.velha.ordem_servico.api.dtos.ordem_servico;

import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.CriarOrdemServicoUseCase;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(name = "OrdemServicoRequest", description = "Dados para criação da Ordem de Serviço")
public record CriarOrdemServicoRequest(
        @NotNull(message = "Veículo Id é obrigatório!")
        @Schema(description = "Id do veículo", example = "4")
        Long veiculoId,

        @NotNull(message = "Proprietário Id é obrigatório!")
        @Schema(description = "Id do proprietário", example = "3")
        Long proprietarioId,

        @Size(max = 500, message = "Observações deve ter no máximo 500 caracteres")
        @Schema(
                description = "Observações da ordem de serviço",
                example = "Proprietário relatou barulho ao frear"
        )
        String reclamacaoProprietario,

        @Schema(description = "Id da peça", example = "3")
        Long pecaId,

        @Schema(description = "quantidade de  peças", example = "3")
        Integer quantidade,

        @Schema(description = "Id do serviço", example = "3")
        Long servicoId,

        @Schema(description = "Valor da mão de obra", example = "200.00")
        BigDecimal valorMaoDeObra

) {
        public CriarOrdemServicoUseCase.Input toCriarOsUseCaseInput(UserId userId) {
                return new CriarOrdemServicoUseCase.Input(veiculoId, proprietarioId, userId, reclamacaoProprietario,pecaId, quantidade, servicoId, valorMaoDeObra);
        }
}