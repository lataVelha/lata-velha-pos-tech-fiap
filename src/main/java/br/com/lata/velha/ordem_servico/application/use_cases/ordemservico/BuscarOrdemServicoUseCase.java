package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BuscarOrdemServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;

    public PaginatedResult<OrdemServicoResponse> execute(Long id,
                                                         StatusOrdemServico status,
                                                         Long proprietarioId,
                                                         Long mecanicoId,
                                                         int page,
                                                         int size) {
        var result = ordemServicoRepository.findByAllOrdemSevico(
                id,
                status != null ? status.name() : null,
                proprietarioId,
                mecanicoId,
                page,
                size
        );

        List<OrdemServicoResponse> content = result.content()
                .stream()
                .map(OrdemServicoResponse::from)
                .toList();

        return new PaginatedResult<>(
                content,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}
