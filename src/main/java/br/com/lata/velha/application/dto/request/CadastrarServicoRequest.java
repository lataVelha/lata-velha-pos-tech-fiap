package br.com.lata.velha.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CadastrarServicoRequest(
        @NotBlank(message = "Nome é obrigatório") String nome,
        @NotBlank(message = "Descrição é obrigatória") String descricao
) {
}
