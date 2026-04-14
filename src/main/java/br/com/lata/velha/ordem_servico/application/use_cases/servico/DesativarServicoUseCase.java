package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.ordem_servico.domain.repositories.ServicoRepository;
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
