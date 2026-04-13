package br.com.lata.velha.ordemDeServico.application.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AtualizarPecaRequest(
    @NotBlank(message = "Nome é obrigatório") String nome,
    @NotBlank(message = "Descrição é obrigatória") String descricao,
    @NotNull(message = "Valor é obrigatório") BigDecimal valor
) {}
