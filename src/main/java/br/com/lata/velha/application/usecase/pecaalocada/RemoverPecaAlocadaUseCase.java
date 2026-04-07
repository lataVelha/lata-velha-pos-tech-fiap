package br.com.lata.velha.application.usecase.pecaalocada;

import br.com.lata.velha.domain.repository.PecaAlocadaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverPecaAlocadaUseCase {

    private final PecaAlocadaRepository pecaAlocadaRepository;

    public void execute(Long id) {
        pecaAlocadaRepository.delete(id);
    }
}