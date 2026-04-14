package br.com.lata.velha.ordem_servico.application.use_cases.pecaalocada;

import br.com.lata.velha.ordem_servico.application.assemblers.PecaAlocadaAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaAlocadaResponse;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaAlocadaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BuscarPecasAlocadasUseCase {

    private final PecaAlocadaRepository pecaAlocadaRepository;

    public PaginatedResult<PecaAlocadaResponse> execute(Long servicoOsId, int page, int size) {
        var pageResult = pecaAlocadaRepository.findByServicoOsId(servicoOsId, page, size);
        
        var dtoList = pageResult.content().stream()
                .map(PecaAlocadaAssembler::toResponse)
                .collect(Collectors.toList());

        return new PaginatedResult<>(
                dtoList,
                pageResult.page(),
                pageResult.size(),
                pageResult.totalElements(),
                pageResult.totalPages()
        );
    }
}