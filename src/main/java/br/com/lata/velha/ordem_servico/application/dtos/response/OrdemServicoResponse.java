package br.com.lata.velha.ordem_servico.application.dtos.response;

import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.infrastructure.repositories.projection.OrdemServicoProjection;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
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
        List<ExecucaoServicoResponse> servicos,

        @Schema(description = "Total dos Serviços", example = "150.00")
        BigDecimal totalServicos,

        @Schema(description = "Total das Peças", example = "150.00")
        BigDecimal totalPecas,

        @Schema(description = "Total da Ordem de Serviço", example = "150.00")
        BigDecimal totalOrdemServico

) {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    public static OrdemServicoResponse from(
            OrdemServico domain,
            String proprietarioNome,
            String veiculoDescricao,
            BigDecimal totalServicos,
            BigDecimal totalPecas,
            BigDecimal totalOrdemServico
    ) {
        List<ExecucaoServicoResponse> servicos = domain.getExecucaoServicos() != null
                ? domain.getExecucaoServicos().stream()
                        .map(ExecucaoServicoResponse::from)
                        .toList()
                : List.of();

        return new OrdemServicoResponse(
                domain.getId(),
                domain.getAtendenteInicioId(),
                null,
                domain.getVeiculoId(),
                veiculoDescricao,
                domain.getProprietarioId(),
                proprietarioNome,
                domain.getMecanicoResponsavelId(),
                null,
                domain.getStatus() != null ? domain.getStatus().name() : null,
                domain.getReclamacaoCliente(),
                domain.getIniciadoEm(),
                domain.getFinalizadoEm(),
                domain.getEntregueEm(),
                domain.getAtualizadoEm(),
                servicos,
                totalServicos,
                totalPecas,
                totalOrdemServico
        );
    }

    public static OrdemServicoResponse from(OrdemServicoProjection p) {
        List<ExecucaoServicoResponse> servicos = List.of();
        try {
            if (p.getServicos() != null && !p.getServicos().isBlank()) {
                servicos = MAPPER.readValue(
                        p.getServicos(),
                        new TypeReference<List<ExecucaoServicoResponse>>() {}
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter serviços JSON", e);
        }

        return new OrdemServicoResponse(
                p.getId(),
                p.getAtendenteInicioId(),
                p.getAtendenteNome(),
                p.getVeiculoId(),
                p.getVeiculoDescricao(),
                p.getProprietarioId(),
                p.getProprietarioNome(),
                p.getMecanicoFinalId(),
                p.getMecanicoNome(),
                p.getStatus(),
                p.getReclamacaoCliente(),
                p.getIniciadoEm(),
                p.getFinalizadoEm(),
                p.getEntregueEm(),
                p.getAtualizadoEm(),
                servicos,
                null,
                null,
                null
        );
    }
}
