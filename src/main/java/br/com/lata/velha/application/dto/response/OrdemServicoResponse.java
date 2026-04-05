package br.com.lata.velha.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "OrdemServicoResponse", description = "Resposta da Ordem de Serviço")
public class OrdemServicoResponse {

    @Schema(description = "Id da ordem de serviço", example = "100")
    private Long id;

    @Schema(description = "Id do Atendente", example = "1")
    private Long atendenteInicioId;

    @Schema(description = "Nome Atendente", example = "Maria Clara")
    private String atendenteNome;

    @Schema(description = "Id do veículo", example = "1")
    private Long veiculoId;

    @Schema(description = "Descrição do veículo", example = "Honda Civic 2018")
    private String veiculoDescricao;

    @Schema(description = "Id do proprietário", example = "10")
    private Long proprietarioId;

    @Schema(description = "Nome do proprietário", example = "João da Silva")
    private String proprietarioNome;

    @Schema(description = "Id do mecanico", example = "10")
    private Long mecanicoFinalId;

    @Schema(description = "Nome do mecanico", example = "João da Silva")
    private String mecanicoNome;

    @Schema(description = "Status da ordem", example = "ABERTA")
    private String status;

    @Schema(description = "Reclamaçâo do Cliente", example = "Trocar pastilhas de freio")
    private String reclamacaoCliente;

    @Schema(description = "Data de início", example = "2026-03-30T11:00:00")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
    private LocalDateTime iniciadoEm;

    @Schema(description = "Data de finalização", example = "2026-03-30T15:30:00")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
    private LocalDateTime finalizadoEm;

    @Schema(description = "Data da Entrega", example = "2026-03-30T15:30:00")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
    private LocalDateTime entregueEm;

    @Schema(description = "Data da ultima atualização", example = "2026-03-30T15:30:00")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
    private LocalDateTime atualizadoEm;

    @Schema(description = "Lista de serviços da OS")
    private List<ServicoOSResponse> servicos;
}