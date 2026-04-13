package br.com.lata.velha.ordemDeServico.application.useCases.veiculo;

import br.com.lata.velha.ordemDeServico.application.assemblers.VeiculoAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordemDeServico.domain.entities.Veiculo;
import br.com.lata.velha.ordemDeServico.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReativarVeiculoUseCase {

    private final VeiculoRepository repository;
    private final VeiculoAssembler assembler;

    public VeiculoResponse execute(Long id) {
        Veiculo veiculo = repository.findInactiveById(id);
        veiculo.activate();
        Veiculo saved = repository.save(veiculo);
        return assembler.toResponse(saved);
    }
}