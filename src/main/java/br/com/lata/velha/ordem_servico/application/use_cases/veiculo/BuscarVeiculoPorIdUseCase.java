package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuscarVeiculoPorIdUseCase {

    private final VeiculoRepository repository;

    public VeiculoResponse execute(Long id) {
        return VeiculoResponse.from(repository.getActiveById(id));
    }
}
