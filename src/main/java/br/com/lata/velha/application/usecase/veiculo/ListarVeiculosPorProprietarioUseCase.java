package br.com.lata.velha.application.usecase.veiculo;

import br.com.lata.velha.application.assembler.VeiculoAssembler;
import br.com.lata.velha.application.dto.response.VeiculoResponse;
import br.com.lata.velha.domain.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListarVeiculosPorProprietarioUseCase {

    private final VeiculoRepository repository;
    private final VeiculoAssembler assembler;

    public List<VeiculoResponse> execute(Long proprietarioId) {
        return repository.findByProprietarioId(proprietarioId)
                .stream()
                .map(assembler::toResponse)
                .toList();
    }
}