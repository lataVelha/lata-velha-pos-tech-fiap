package br.com.lata.velha.ordemDeServico.application.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "OrdemServicoResponse", description = "Resposta da Ordem de Serviço")
public record OrdemServicoResponse(

        @Schema(description = "Id da ordem de serviço", example = "100")
        Long id,

        @Schema(description = "Id do Atendente", example = "1")
        Long atendenteInicioId,

        @Schema(description = "Nome Atendente", example = "Maria Clara")
        String atendenteNome,

        @Schema(description = "Id do veículo", example = "1")
        Long veiculoId,

        @Schema(description = "Descrição do veículo", example = "Honda Civic 2018")
        String veiculoDescricao,

        @Schema(description = "Id do proprietário", example = "10")
        Long proprietarioId,

        @Schema(description = "Nome do proprietário", example = "João da Silva")
        String proprietarioNome,

        @Schema(description = "Id do mecanico", example = "10")
        Long mecanicoFinalId,

        @Schema(description = "Nome do mecanico", example = "João da Silva")
        String mecanicoNome,

        @Schema(description = "Status da ordem", example = "ABERTA")
        String status,

        @Schema(description = "Reclamaçâo do Cliente", example = "Trocar pastilhas de freio")
        String reclamacaoCliente,

        @Schema(description = "Data de início", example = "2026-03-30T11:00:00")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
        LocalDateTime iniciadoEm,

        @Schema(description = "Data de finalização", example = "2026-03-30T15:30:00")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
        LocalDateTime finalizadoEm,

        @Schema(description = "Data da Entrega", example = "2026-03-30T15:30:00")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
        LocalDateTime entregueEm,

        @Schema(description = "Data da ultima atualização", example = "2026-03-30T15:30:00")
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
        LocalDateTime atualizadoEm,

        @Schema(description = "Lista de serviços da OS")
        List<ServicoOSResponse> servicos

) {}