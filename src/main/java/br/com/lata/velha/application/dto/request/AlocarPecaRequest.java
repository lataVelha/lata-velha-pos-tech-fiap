package br.com.lata.velha.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AlocarPecaRequest(
        @NotNull(message = "O ID do Serviço é obrigatório") Long servicoOsId,
        @NotNull(message = "O ID da Peça é obrigatório") Long pecaId,
        @NotNull(message = "A quantidade é obrigatória") @Positive(message = "A quantidade deve ser maior que zero") Integer quantidade
) {}