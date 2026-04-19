package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DesativarVeiculoUseCase {

    private final VeiculoRepository repository;

    public void execute(Long id) {
        Veiculo veiculo = repository.getActiveById(id);
        veiculo.deactivate();
        repository.save(veiculo);
    }
}