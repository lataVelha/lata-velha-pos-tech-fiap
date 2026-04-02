package br.com.lata.velha.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
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

    @Schema(description = "Observações", example = "Trocar pastilhas de freio")
    private String observacoes;

    @Schema(description = "Data de criação", example = "2026-03-30T10:15:30")
    private LocalDateTime criadaEm;

    @Schema(description = "Data de início", example = "2026-03-30T11:00:00")
    private LocalDateTime iniciadoEm;

    @Schema(description = "Data de finalização", example = "2026-03-30T15:30:00")
    private LocalDateTime finalizadoEm;

    @Schema(description = "Data da Entrega", example = "2026-03-30T15:30:00")
    private LocalDateTime entregueEm;

    @Schema(description = "Data da ultima atualização", example = "2026-03-30T15:30:00")
    private LocalDateTime atualizadoEm;

    @Schema(description = "Lista de serviços da OS")
    private List<ServicoOSResponse> servicos;
}