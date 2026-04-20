package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReativarVeiculoUseCase {

    private final VeiculoRepository repository;

    public VeiculoResponse execute(Long id) {
        Veiculo veiculo = repository.findInactiveById(id);
        veiculo.activate();
        return VeiculoResponse.from(repository.save(veiculo));
    }
}
