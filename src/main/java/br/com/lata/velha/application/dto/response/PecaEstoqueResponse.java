package br.com.lata.velha.application.dto.response;

public record PecaEstoqueResponse(
        Long pecaId,
        Integer quantidadeArmazenada
) {
}
