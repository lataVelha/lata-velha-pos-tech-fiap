package br.com.lata.velha.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CadastrarFuncionarioRequest(
    @NotBlank(message = "Nome é obrigatório") String nome,
    @NotBlank(message = "Username é obrigatório") String username,
    @NotBlank(message = "Senha é obrigatória") String senha,
    @NotNull(message = "Cargo ID é obrigatório") Long cargoId
) {}