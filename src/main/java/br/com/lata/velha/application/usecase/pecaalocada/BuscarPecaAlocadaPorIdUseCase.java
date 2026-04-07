package br.com.lata.velha.application.usecase.pecaalocada;

import br.com.lata.velha.application.assembler.PecaAlocadaAssembler;
import br.com.lata.velha.application.dto.response.PecaAlocadaResponse;
import br.com.lata.velha.domain.repository.PecaAlocadaRepository;
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