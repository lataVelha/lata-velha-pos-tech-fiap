package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.application.dtos.request.VeiculoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CriarVeiculoUseCase {

    private final VeiculoRepository veiculoRepository;
    private final ProprietarioRepository proprietarioRepository;

    public VeiculoResponse execute(VeiculoRequest request) {
        proprietarioRepository.findActiveById(request.proprietarioId());
        return VeiculoResponse.from(veiculoRepository.save(request.toDomain()));
    }
}
