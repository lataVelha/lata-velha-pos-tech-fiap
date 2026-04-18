package br.com.lata.velha.ordem_servico.application.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AddServicoRequest(
        @NotNull(message = "ID Os é obrigatório") Long idOs,
        @NotEmpty(message = "Lista servicoOSId é obrigatória")
        List<@Valid ServicoRequest> servicoRequests){

}