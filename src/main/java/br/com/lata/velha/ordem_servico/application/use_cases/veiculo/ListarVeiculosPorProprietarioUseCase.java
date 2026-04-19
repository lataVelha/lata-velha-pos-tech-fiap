package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListarVeiculosPorProprietarioUseCase {

    private final VeiculoRepository repository;

    public List<VeiculoResponse> execute(Long proprietarioId) {
        return repository.findActiveByProprietarioId(proprietarioId)
                .stream()
                .map(VeiculoResponse::from)
                .toList();
    }
}
