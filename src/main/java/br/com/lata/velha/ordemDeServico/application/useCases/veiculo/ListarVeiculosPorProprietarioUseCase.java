package br.com.lata.velha.ordemDeServico.application.useCases.veiculo;

import br.com.lata.velha.ordemDeServico.application.assemblers.VeiculoAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordemDeServico.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListarVeiculosPorProprietarioUseCase {

    private final VeiculoRepository repository;
    private final VeiculoAssembler assembler;

    public List<VeiculoResponse> execute(Long proprietarioId) {
        return repository.findActiveByProprietarioId(proprietarioId)
                .stream()
                .map(assembler::toResponse)
                .toList();
    }
}