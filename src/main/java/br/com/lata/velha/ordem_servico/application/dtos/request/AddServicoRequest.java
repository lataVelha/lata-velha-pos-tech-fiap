package br.com.lata.velha.ordem_servico.application.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(name = "AddServicoRequest", description = "Dados para adição de serviços a uma ordem de serviço")
public record AddServicoRequest(

        @NotNull(message = "ID Os é obrigatório")
        @Schema(description = "ID da ordem de serviço", example = "20")
        Long idOs,

        @NotEmpty(message = "Lista servicoOSId é obrigatória")
        @Schema(description = "Lista de serviços a adicionar")
        List<@Valid ServicoRequest> servicoRequests

) {}
