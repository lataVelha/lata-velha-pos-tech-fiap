package br.com.lata.velha.ordemDeServico.application.useCases.pecaalocada;

import br.com.lata.velha.ordemDeServico.application.assemblers.PecaAlocadaAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.response.PecaAlocadaResponse;
import br.com.lata.velha.ordemDeServico.domain.repositories.PecaAlocadaRepository;
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