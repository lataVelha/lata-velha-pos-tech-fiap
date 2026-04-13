package br.com.lata.velha.ordemDeServico.application.useCases.servico;

import br.com.lata.velha.ordemDeServico.domain.entities.Servico;
import br.com.lata.velha.ordemDeServico.domain.repositories.ServicoRepository;
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
