package br.com.lata.velha.ordemDeServico.application.useCases.peca;

import br.com.lata.velha.ordemDeServico.domain.entities.Peca;
import br.com.lata.velha.ordemDeServico.domain.repositories.PecaRepository;
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
