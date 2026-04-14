package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.application.assemblers.PaginatedAssembler;
import br.com.lata.velha.ordem_servico.application.assemblers.ServicoAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordem_servico.domain.repositories.ServicoRepository;
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
