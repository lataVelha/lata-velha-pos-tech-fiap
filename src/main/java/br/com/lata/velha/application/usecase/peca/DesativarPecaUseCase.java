package br.com.lata.velha.application.usecase.peca;

import br.com.lata.velha.domain.model.Peca;
import br.com.lata.velha.domain.repository.PecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DesativarPecaUseCase {

    private final PecaRepository repository;

    public void execute(Long id) {
        Peca peca = repository.findActiveById(id);

        peca.desativar();
        repository.save(peca);
    }
}
