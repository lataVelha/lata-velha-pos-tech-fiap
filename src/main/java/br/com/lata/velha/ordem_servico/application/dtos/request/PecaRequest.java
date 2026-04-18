package br.com.lata.velha.ordem_servico.application.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record PecaRequest(
        @NotNull(message = "Peça Id é obrigatório!")
        @Schema(example = "1", description = "id da peça")
        Long pecaId,

        @NotNull(message = "Quantidade é obrigatório!")
        @Schema(example = "1",description = "quantidade de peças neecessarias")
        Integer quantidade
) {


}
