package br.com.lata.velha.application.usecase.veiculo;

import br.com.lata.velha.application.assembler.VeiculoAssembler;
import br.com.lata.velha.application.dto.response.VeiculoResponse;
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

    public List<VeiculoResponse> execute() {
        return repository.listarTodos().stream().map(assembler::toResponse).toList();
    }
}