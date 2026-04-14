package br.com.lata.velha.ordem_servico.application.dtos.response;

public record ServicoResponse(
        Long id,
        String nome,
        String descricao
) {
}
