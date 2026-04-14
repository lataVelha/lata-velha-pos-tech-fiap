package br.com.lata.velha.ordem_servico.application.dtos.response;

public record PecaEstoqueResponse(
        Long pecaId,
        Integer quantidadeArmazenada
) {
}
