package br.com.lata.velha.ordemDeServico.application.dtos.response;

public record PecaEstoqueResponse(
        Long pecaId,
        Integer quantidadeArmazenada
) {
}
