package br.com.lata.velha.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServicoOSResponse {

    @Schema(example = "189")
    private Long id;

    @Schema(example = "134")
    private Long servicoId;

    @Schema(example = "Balancemento")
    private String servicoNome;

    @Schema(example = "APROVADO")
    private String status;

    @Schema(example = "134")
    private Long mecanicoResponsavelId;

    @Schema(example = "000.00")
    private BigDecimal valorMaoDeObra;

    private LocalDateTime iniciadoEm;
    private LocalDateTime terminadoEm;
    private LocalDateTime atualizadoEm;

    private List<PecaServicoResponse> pecas;
}