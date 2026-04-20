package br.com.lata.velha.ordem_servico.application.dtos.response;

import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;

import java.util.UUID;

public record FuncionarioResponse(
    Long id,
    String nome,
    String cargo,
    UUID userId
) {
    public static FuncionarioResponse from(Funcionario funcionario) {
        return new FuncionarioResponse(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getCargo().getNome(),
                funcionario.getUserId().getValue()
        );
    }
}