package br.com.lata.velha.application.usecase.veiculo;

import br.com.lata.velha.application.assembler.VeiculoAssembler;
import br.com.lata.velha.application.dto.response.VeiculoResponse;
import br.com.lata.velha.domain.entities.Veiculo;
import br.com.lata.velha.domain.repository.VeiculoRepository;
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