package br.com.lata.velha.ordem_servico.application.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AprovarOrdemServicoRequest(
        @NotNull(message = "ID Os é obrigatório") Long idOs,
        @NotNull(message = "ID Funcionario é obrigatório") Long idFunc,
        @NotEmpty(message = "Lista de idServicoOs não pode ser vazia")
        List<@Valid AprovarServicoOsRequest> idServicoOsAprovar) {
}
