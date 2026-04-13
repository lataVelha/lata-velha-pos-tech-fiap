package br.com.lata.velha.ordemDeServico.application.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record PecaRequest(
        @NotNull(message = "Peça Id é obrigatório!")
        @Schema(example = "1")
        Long pecaId,

        @NotNull(message = "Quantidade é obrigatório!")
        @Schema(example = "1")
        Integer quantidade
) {


}
