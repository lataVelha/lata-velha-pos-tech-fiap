package br.com.lata.velha.ordem_servico.application.dtos.response;

import br.com.lata.velha.ordem_servico.application.use_cases.funcionario.AtualizarFuncionarioUseCase;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;

import java.util.UUID;

public record FuncionarioResponse(
    Long id,
    String nome,
    String cargo,
    UUID userId
) {
    public static FuncionarioResponse fromUpdateOutput(AtualizarFuncionarioUseCase.Output output) {
        return new FuncionarioResponse(
                output.id(),
                output.nome(),
                output.cargo(),
                output.userId().getValue()
        );
    }

    public static FuncionarioResponse fromEntity(Funcionario funcionario) {
        return new FuncionarioResponse(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getCargo().getNome(),
                funcionario.getUserId().getValue()
        );
    }
}