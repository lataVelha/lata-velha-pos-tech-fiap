package br.com.lata.velha.ordemDeServico.application.useCases.servico;

import br.com.lata.velha.ordemDeServico.application.assemblers.PaginatedAssembler;
import br.com.lata.velha.ordemDeServico.application.assemblers.ServicoAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordemDeServico.domain.repositories.ServicoRepository;
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
