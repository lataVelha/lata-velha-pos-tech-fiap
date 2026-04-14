package br.com.lata.velha.ordem_servico.application.use_cases.pecaalocada;

import br.com.lata.velha.ordem_servico.application.assemblers.PecaAlocadaAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaAlocadaResponse;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaAlocadaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuscarPecaAlocadaPorIdUseCase {

    private final PecaAlocadaRepository pecaAlocadaRepository;

    public PecaAlocadaResponse execute(Long id) {
        return PecaAlocadaAssembler.toResponse(pecaAlocadaRepository.findById(id));
    }
}