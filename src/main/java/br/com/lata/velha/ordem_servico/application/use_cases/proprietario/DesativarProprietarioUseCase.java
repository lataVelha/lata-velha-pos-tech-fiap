package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DesativarProprietarioUseCase {

    private final ProprietarioRepository repository;

    public void execute(Long id) {
        Proprietario proprietario = repository.getActiveById(id);
        proprietario.deactivate();
        repository.save(proprietario);
    }
}