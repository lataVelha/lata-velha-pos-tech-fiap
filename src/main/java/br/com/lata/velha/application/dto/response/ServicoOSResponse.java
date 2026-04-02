package br.com.lata.velha.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
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
    @Schema(example = "13/05/2025")
    private LocalDateTime iniciadoEm;
    @Schema(example = "13/05/2025")
    private LocalDateTime terminadoEm;
    @Schema(example = "13/05/2025")
    private LocalDateTime atualizadoEm;
}