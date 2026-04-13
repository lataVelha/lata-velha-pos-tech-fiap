package br.com.lata.velha.ordemDeServico.application.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record AjustarPecaEstoqueRequest(
        @NotNull(message = "Quantidade armazenada é obrigatória")
        @PositiveOrZero(message = "Quantidade armazenada não pode ser negativa")
        Integer quantidadeArmazenada
) {
}
