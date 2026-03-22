package br.com.lata.velha.application.usecase.veiculo;

import br.com.lata.velha.application.assembler.VeiculoAssembler;
import br.com.lata.velha.application.dto.response.VeiculoResponse;
import br.com.lata.velha.domain.exception.VeiculoNotFoundException;
import br.com.lata.velha.domain.repository.VeiculoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuscarVeiculoUseCase {

    private final VeiculoRepository repository;
    private final VeiculoAssembler assembler;

    public BuscarVeiculoUseCase(VeiculoRepository repository,
                                VeiculoAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    public VeiculoResponse porId(Long id) {
        return assembler.toResponse(repository.buscarPorId(id)
                .orElseThrow(() -> new VeiculoNotFoundException(id)));
    }

    public List<VeiculoResponse> porProprietario(Long proprietarioId) {
        return repository.listarPorProprietario(proprietarioId).stream()
                .map(assembler::toResponse).toList();
    }
}