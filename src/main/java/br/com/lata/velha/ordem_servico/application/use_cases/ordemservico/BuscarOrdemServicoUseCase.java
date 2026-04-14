package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.assemblers.OrdemServicoAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.infrastructure.repositories.projection.OrdemServicoProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BuscarOrdemServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;

    private final OrdemServicoAssembler ordemServicoAssembler;

    public PaginatedResult<OrdemServicoResponse> execute(Long id,
                                                         StatusOrdemServico status,
                                                         Long proprietarioId,
                                                         Long mecanicoId,
                                                         int page,
                                                         int size) {

        Page<OrdemServicoProjection> result =
                ordemServicoRepository.findByAllOrdemSevico(
                        id,
                        status != null ? status.name() : null,
                        proprietarioId,
                        mecanicoId,
                        PageRequest.of(page, size)
                );

        List<OrdemServicoResponse> content =
                result.getContent()
                        .stream()
                        .map(ordemServicoAssembler::map)
                        .toList();

        return new PaginatedResult<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }
}
