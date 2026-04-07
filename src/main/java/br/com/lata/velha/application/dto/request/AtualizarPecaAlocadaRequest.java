package br.com.lata.velha.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AtualizarPecaAlocadaRequest(
        @NotNull(message = "A quantidade é obrigatória") @Positive(message = "A quantidade deve ser maior que zero") Integer quantidade
) {}