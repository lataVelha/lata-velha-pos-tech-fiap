package br.com.lata.velha.application.usecase.veiculo;

import br.com.lata.velha.domain.entities.Veiculo;
import br.com.lata.velha.domain.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DesativarVeiculoUseCase {

    private final VeiculoRepository repository;

    public void execute(Long id) {
        Veiculo veiculo = repository.findActiveById(id);
        veiculo.deactivate();
        repository.save(veiculo);
    }
}