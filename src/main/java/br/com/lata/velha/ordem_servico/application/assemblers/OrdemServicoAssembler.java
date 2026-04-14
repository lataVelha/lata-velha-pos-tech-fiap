package br.com.lata.velha.ordem_servico.application.assemblers;

import br.com.lata.velha.ordem_servico.application.dtos.request.OrdemServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.AprovarOrdemServicoResponse;
import br.com.lata.velha.ordem_servico.application.dtos.response.AprovarServicoOsResponse;
import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.application.dtos.response.ServicoOSResponse;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.infrastructure.repositories.projection.OrdemServicoProjection;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrdemServicoAssembler {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public OrdemServico toDomain(OrdemServicoRequest request) {
        return new OrdemServico(
                null,
                request.proprietarioId(),
                request.veiculoId(),
                request.reclamacaoCliente(),
                request.atendenteInicioId()
        );
    }

    public OrdemServicoResponse toResponse(
            OrdemServico domain,
            String proprietarioNome,
            String veiculoDescricao
    ) {

        List<ServicoOSResponse> servicos =
                domain.getServicos() != null
                        ? domain.getServicos()
                        .stream()
                        .map(ServicoOSAssembler::toResponse)
                        .collect(Collectors.toList())
                        : Collections.emptyList();

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
                servicos
        );
    }

    public OrdemServicoResponse map(OrdemServicoProjection p) {

        List<ServicoOSResponse> servicos = Collections.emptyList();

        try {
            if (p.getServicos() != null && !p.getServicos().isBlank()) {
                servicos = mapper.readValue(
                        p.getServicos(),
                        new TypeReference<List<ServicoOSResponse>>() {}
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
                servicos
        );
    }

    public AprovarOrdemServicoResponse toAprovarResponse(OrdemServico domain) {

        List<AprovarServicoOsResponse> servicos =
                domain.getServicos()
                        .stream()
                        .map(s -> new AprovarServicoOsResponse(
                                s.getId(),
                                s.getStatus().name()
                        ))
                        .collect(Collectors.toList());

        return new AprovarOrdemServicoResponse(
                domain.getId(),
                domain.getStatus().name(),
                servicos
        );
    }
}