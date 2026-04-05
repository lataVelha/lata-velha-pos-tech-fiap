package br.com.lata.velha.application.assembler;

import br.com.lata.velha.application.dto.request.OrdemServicoRequest;
import br.com.lata.velha.application.dto.response.AprovarOrdemServicoResponse;
import br.com.lata.velha.application.dto.response.AprovarServicoOsResponse;
import br.com.lata.velha.application.dto.response.OrdemServicoResponse;
import br.com.lata.velha.application.dto.response.ServicoOSResponse;
import br.com.lata.velha.domain.model.OrdemServico;
import br.com.lata.velha.infrastructure.repository.projection.OrdemServicoProjection;
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

        return OrdemServicoResponse.builder()
                .id(domain.getId())
                .veiculoId(domain.getVeiculoId())
                .veiculoDescricao(veiculoDescricao)
                .proprietarioId(domain.getProprietarioId())
                .proprietarioNome(proprietarioNome)
                .status(domain.getStatus() != null ? domain.getStatus().name() : null)
                .reclamacaoCliente(domain.getReclamacaoCliente())
                .iniciadoEm(domain.getIniciadoEm())
                .finalizadoEm(domain.getFinalizadoEm())
                .entregueEm(domain.getEntregueEm())
                .atualizadoEm(domain.getAtualizadoEm())
                .atendenteInicioId(domain.getAtendenteInicioId())
                .mecanicoFinalId(domain.getMecanicoFinalId())
                .servicos(
                        domain.getServicos() != null
                                ? domain.getServicos()
                                .stream()
                                .map(ServicoOSAssembler::toResponse)
                                .collect(Collectors.toList())
                                : Collections.emptyList()
                )
                .build();
    }

    public OrdemServicoResponse map(OrdemServicoProjection p) {

        List<ServicoOSResponse> servicos = Collections.emptyList();

        try {
            if (p.getServicos() != null) {
                servicos = mapper.readValue(
                        p.getServicos(),
                        new TypeReference<List<ServicoOSResponse>>() {});
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter serviços JSON", e);
        }

        return OrdemServicoResponse.builder()
                .id(p.getId())
                .atendenteInicioId(p.getAtendenteInicioId())
                .atendenteNome(p.getAtendenteNome())
                .veiculoId(p.getVeiculoId())
                .veiculoDescricao(p.getVeiculoDescricao())
                .proprietarioId(p.getProprietarioId())
                .proprietarioNome(p.getProprietarioNome())
                .mecanicoFinalId(p.getMecanicoFinalId())
                .mecanicoNome(p.getMecanicoNome())
                .status(p.getStatus())
                .reclamacaoCliente(p.getReclamacaoCliente())
                .iniciadoEm(p.getIniciadoEm())
                .finalizadoEm(p.getFinalizadoEm())
                .entregueEm(p.getEntregueEm())
                .atualizadoEm(p.getAtualizadoEm())
                .servicos(servicos)
                .build();
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