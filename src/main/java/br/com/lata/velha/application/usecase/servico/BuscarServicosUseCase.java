package br.com.lata.velha.application.usecase.servico;

import br.com.lata.velha.application.assembler.PaginatedAssembler;
import br.com.lata.velha.application.assembler.ServicoAssembler;
import br.com.lata.velha.application.dto.response.ServicoResponse;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.domain.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuscarServicosUseCase {

    private final ServicoRepository repository;
    private final ServicoAssembler assembler;
    private final PaginatedAssembler paginatedAssembler;

    public PaginatedResult<ServicoResponse> execute(int page, int size) {
        return paginatedAssembler.toResponse(
                repository.findAllActivePaginated(page, size),
                assembler::toResponse
        );
    }
}
