package br.com.lata.velha.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServicoOSRequest {

    @NotNull(message = "Serviço Id é obrigatório!")
    @Schema(example = "Serviço Id")
    private Long servicoId;

    @NotNull(message = "Ordem de Serviço Id é obrigatório!")
    @Schema(example = "Ordem de Serviço Id ")
    private Long ordemServicoId;

    @NotNull(message = "Valor de Mão de Obra é obrigatório!")
    @Schema(example = "000.00")
    private BigDecimal valorMaoDeObra;
}