package br.com.lata.velha.ordemDeServico.application.useCases.veiculo;

import br.com.lata.velha.ordemDeServico.application.assemblers.PaginatedAssembler;
import br.com.lata.velha.ordemDeServico.application.assemblers.VeiculoAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordemDeServico.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListarVeiculosUseCase {

    private final VeiculoRepository repository;
    private final VeiculoAssembler veiculoAssembler;
    private final PaginatedAssembler paginatedAssembler;

    public PaginatedResult<VeiculoResponse> execute(int page, int size) {
        return paginatedAssembler.toResponse(
                repository.findAllActivePaginated(page, size),
                veiculoAssembler::toResponse
        );
    }
}