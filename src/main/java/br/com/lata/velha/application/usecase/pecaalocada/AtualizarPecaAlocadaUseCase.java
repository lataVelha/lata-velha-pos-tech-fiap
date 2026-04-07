package br.com.lata.velha.application.usecase.pecaalocada;

import br.com.lata.velha.application.assembler.PecaAlocadaAssembler;
import br.com.lata.velha.application.dto.request.AtualizarPecaAlocadaRequest;
import br.com.lata.velha.application.dto.response.PecaAlocadaResponse;
import br.com.lata.velha.domain.model.PecaAlocada;
import br.com.lata.velha.domain.repository.PecaAlocadaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AtualizarPecaAlocadaUseCase {

    private final PecaAlocadaRepository pecaAlocadaRepository;

    public PecaAlocadaResponse execute(Long id, AtualizarPecaAlocadaRequest request) {
        
        PecaAlocada pecaAlocada = pecaAlocadaRepository.findById(id);
        if (pecaAlocada == null) {
            throw new IllegalArgumentException("Peça alocada não encontrada");
        }

        pecaAlocada.setQuantidadeAlocada(request.quantidade());

        PecaAlocada saved = pecaAlocadaRepository.save(pecaAlocada);

        return PecaAlocadaAssembler.toResponse(saved);
    }
}