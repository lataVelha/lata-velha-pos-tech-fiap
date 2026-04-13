package br.com.lata.velha.application.usecase.ordemservico;

import br.com.lata.velha.application.assembler.OrdemServicoAssembler;
import br.com.lata.velha.application.dto.response.OrdemServicoResponse;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.domain.enuns.StatusOrdemServico;
import br.com.lata.velha.domain.repository.OrdemServicoRepository;
import br.com.lata.velha.infrastructure.repository.projection.OrdemServicoProjection;
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
