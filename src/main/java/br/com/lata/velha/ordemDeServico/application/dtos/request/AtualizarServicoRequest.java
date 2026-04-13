package br.com.lata.velha.ordemDeServico.application.dtos.request;

import jakarta.validation.constraints.NotBlank;

public record AtualizarServicoRequest(
        @NotBlank(message = "Nome é obrigatório") String nome,
        @NotBlank(message = "Descrição é obrigatória") String descricao
) {
}
