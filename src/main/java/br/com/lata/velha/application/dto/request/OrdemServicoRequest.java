package br.com.lata.velha.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(name = "OrdemServicoRequest", description = "Dados para criação da Ordem de Serviço")
public class OrdemServicoRequest {

    @NotNull(message = "Veículo Id é obrigatório!")
    @Schema(description = "Id do veículo", example = "1")
    private Long veiculoId;

    @NotNull(message = "Proprietário Id é obrigatório!")
    @Schema(description = "Id do proprietário", example = "10")
    private Long proprietarioId;

    @NotNull(message = "Atendente Id é obrigatório!")
    @Schema(description = "Id do atendente", example = "10")
    private Long atendenteInicioId;

    @Size(max = 500, message = "Observações deve ter no máximo 500 caracteres")
    @Schema(description = "Observações da ordem de serviço",
            example = "Cliente relatou barulho ao frear")
    private String reclamacaoCliente;
}