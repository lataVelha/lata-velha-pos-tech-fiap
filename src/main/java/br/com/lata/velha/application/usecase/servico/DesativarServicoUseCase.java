package br.com.lata.velha.application.usecase.servico;

import br.com.lata.velha.domain.entities.Servico;
import br.com.lata.velha.domain.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DesativarServicoUseCase {

    private final ServicoRepository repository;

    public void execute(Long id) {
        Servico servico = repository.findActiveById(id);

        servico.desativar();
        repository.save(servico);
    }
}
