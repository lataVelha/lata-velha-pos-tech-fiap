package br.com.lata.velha.application.usecase.peca;

import br.com.lata.velha.application.assembler.PaginatedAssembler;
import br.com.lata.velha.application.assembler.PecaAssembler;
import br.com.lata.velha.application.dto.response.PecaResponse;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.domain.repository.PecaRepository;
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
