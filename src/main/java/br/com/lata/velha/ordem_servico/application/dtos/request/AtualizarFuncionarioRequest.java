package br.com.lata.velha.ordem_servico.application.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizarFuncionarioRequest(
    @NotBlank(message = "Nome é obrigatório") String nome,
    @NotNull(message = "Cargo ID é obrigatório") Long cargoId
) {}