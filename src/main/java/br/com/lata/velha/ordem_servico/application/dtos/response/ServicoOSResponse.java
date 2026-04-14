package br.com.lata.velha.ordem_servico.application.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ServicoOSResponse(

        @Schema(example = "189")
        Long id,

        @Schema(example = "134")
        Long servicoId,

        @Schema(example = "Balanceamento")
        String servicoNome,

        @Schema(example = "APROVADO")
        String status,

        @Schema(example = "134")
        Long mecanicoResponsavelId,

        @Schema(example = "150.00")
        BigDecimal valorMaoDeObra,

        LocalDateTime iniciadoEm,
        LocalDateTime terminadoEm,
        LocalDateTime atualizadoEm,

        List<PecaServicoResponse> pecas

) {}