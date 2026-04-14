package br.com.lata.velha.ordem_servico.application.dtos.response;

import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;

public record FuncionarioResponse(
    Long id,
    String nome,
    String cargo
) {
    public static FuncionarioResponse fromEntity(Funcionario funcionario) {
        return new FuncionarioResponse(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getCargo().getNome()
        );
    }
}