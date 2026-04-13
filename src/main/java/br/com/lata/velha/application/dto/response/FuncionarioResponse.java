package br.com.lata.velha.application.dto.response;

import br.com.lata.velha.domain.model.Funcionario;

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