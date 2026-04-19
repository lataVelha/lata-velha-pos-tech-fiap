package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.application.dtos.request.VeiculoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AtualizarVeiculoUseCase {

    private final VeiculoRepository veiculoRepository;
    private final ProprietarioRepository proprietarioRepository;

    public VeiculoResponse execute(Long id, VeiculoRequest request) {
        Veiculo existing = veiculoRepository.getActiveById(id);
        proprietarioRepository.getActiveById(request.proprietarioId());
        request.updateDomain(existing);
        return VeiculoResponse.from(veiculoRepository.save(existing));
    }
}
