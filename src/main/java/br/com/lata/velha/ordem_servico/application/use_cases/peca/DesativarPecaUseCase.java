package br.com.lata.velha.ordem_servico.application.use_cases.peca;

import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DesativarPecaUseCase {

    private final PecaRepository repository;

    public void execute(Long id) {
        Peca peca = repository.getActiveById(id);

        peca.desativar();
        repository.save(peca);
    }
}
