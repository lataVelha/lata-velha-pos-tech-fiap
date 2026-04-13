package br.com.lata.velha.ordemDeServico.application.dtos.response;

public record ServicoResponse(
        Long id,
        String nome,
        String descricao
) {
}
