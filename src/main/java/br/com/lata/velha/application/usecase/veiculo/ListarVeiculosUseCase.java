package br.com.lata.velha.application.usecase.veiculo;

import br.com.lata.velha.application.assembler.VeiculoAssembler;
import br.com.lata.velha.application.dto.response.PaginatedResponse;
import br.com.lata.velha.application.dto.response.VeiculoResponse;
import br.com.lata.velha.domain.model.Veiculo;
import br.com.lata.velha.domain.common.PaginatedResult;
import br.com.lata.velha.domain.repository.VeiculoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarVeiculosUseCase {

    private final VeiculoRepository repository;
    private final VeiculoAssembler assembler;

    public ListarVeiculosUseCase(VeiculoRepository repository,
                                 VeiculoAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    public PaginatedResponse<VeiculoResponse> execute(int page, int size) {
        PaginatedResult<Veiculo> resultado = repository.listarPaginado(page, size);

        List<VeiculoResponse> content = resultado.content()
                .stream()
                .map(assembler::toResponse)
                .toList();

        return new PaginatedResponse<>(
                content,
                resultado.page(),
                resultado.size(),
                resultado.totalElements(),
                resultado.totalPages()
        );
    }
}