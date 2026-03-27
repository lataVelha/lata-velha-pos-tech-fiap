package br.com.lata.velha.application.usecase.veiculo;

import br.com.lata.velha.application.assembler.PaginatedAssembler;
import br.com.lata.velha.application.assembler.VeiculoAssembler;
import br.com.lata.velha.application.dto.response.PaginatedResponse;
import br.com.lata.velha.application.dto.response.VeiculoResponse;
import br.com.lata.velha.domain.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListarVeiculosUseCase {

    private final VeiculoRepository repository;
    private final VeiculoAssembler veiculoAssembler;
    private final PaginatedAssembler paginatedAssembler;

    public PaginatedResponse<VeiculoResponse> execute(int page, int size) {
        return paginatedAssembler.toResponse(
                repository.findAllActivePaginated(page, size),
                veiculoAssembler::toResponse
        );
    }
}