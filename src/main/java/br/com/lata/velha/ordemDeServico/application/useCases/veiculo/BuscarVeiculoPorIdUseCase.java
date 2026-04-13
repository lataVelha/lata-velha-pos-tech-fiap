package br.com.lata.velha.ordemDeServico.application.useCases.veiculo;

import br.com.lata.velha.ordemDeServico.application.assemblers.VeiculoAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordemDeServico.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuscarVeiculoPorIdUseCase {

    private final VeiculoRepository repository;
    private final VeiculoAssembler assembler;

    public VeiculoResponse execute(Long id) {
        return assembler.toResponse(repository.findActiveById(id));
    }
}