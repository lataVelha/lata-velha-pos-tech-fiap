package br.com.lata.velha.ordemDeServico.application.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ServicoOSRequest(

        @NotNull(message = "Serviço Id é obrigatório!")
        @Schema(example = "1")
        Long servicoId,

        @Schema(example = "[1,2,3]")
        List<PecaRequest> pecas,

        @NotNull(message = "Valor de Mão de Obra é obrigatório!")
        @Schema(example = "150.00")
        BigDecimal valorMaoDeObra

) {}
