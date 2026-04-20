package br.com.lata.velha.ordem_servico.application.dtos.response;

import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ExecucaoServicoResponse(

        @Schema(example = "189")
        Long id,

        @Schema(example = "134")
        Long servicoId,

        @Schema(example = "Balanceamento")
        String servicoNome,

        @Schema(example = "APROVADO")
        StatusExecucaoServico status,

        @Schema(example = "134")
        Long mecanicoResponsavelId,

        @Schema(example = "150.00")
        BigDecimal valorMaoDeObra,

        LocalDateTime iniciadoEm,
        LocalDateTime terminadoEm,
        LocalDateTime atualizadoEm,

        List<PecaServicoResponse> pecas

) {
    public static ExecucaoServicoResponse from(ExecucaoServico domain) {
        return new ExecucaoServicoResponse(
                domain.getId(),
                domain.getServico().getId(),
                domain.getServico().getNome(),
                domain.getStatus(),
                domain.getMecanicoResponsavelId(),
                domain.getValorMaoDeObra(),
                domain.getIniciadoEm(),
                domain.getTerminadoEm(),
                domain.getAtualizadoEm(),
                null
        );
    }
}
