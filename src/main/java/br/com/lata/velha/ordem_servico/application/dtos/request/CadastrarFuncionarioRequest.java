package br.com.lata.velha.ordem_servico.application.dtos.request;

import br.com.lata.velha.ordem_servico.application.use_cases.funcionario.CadastrarFuncionarioUseCase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CadastrarFuncionarioRequest(
    @NotBlank(message = "Nome é obrigatório") String nome,
    @NotBlank(message = "Username é obrigatório") String username,
    @NotBlank(message = "Senha é obrigatória") String senha,
    @NotNull(message = "Cargo ID é obrigatório") Long cargoId
) {
    public CadastrarFuncionarioUseCase.Input toCadastrarInput() {
        return new CadastrarFuncionarioUseCase.Input(
                nome,
                username,
                senha,
                cargoId
        );
    }
}