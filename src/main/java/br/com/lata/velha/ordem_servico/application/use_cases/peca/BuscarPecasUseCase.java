package br.com.lata.velha.ordem_servico.application.use_cases.peca;

import br.com.lata.velha.ordem_servico.application.assemblers.PaginatedAssembler;
import br.com.lata.velha.ordem_servico.application.assemblers.PecaAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaResponse;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuscarPecasUseCase {

    private final PecaRepository repository;
    private final PecaAssembler assembler;
    private final PaginatedAssembler paginatedAssembler;

    public PaginatedResult<PecaResponse> execute(int page, int size) {
        return paginatedAssembler.toResponse(
                repository.findAllActivePaginated(page, size),
                assembler::toResponse
        );
    }
}
