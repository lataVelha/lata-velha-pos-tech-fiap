package br.com.lata.velha.ordemDeServico.application.useCases.proprietario;

import br.com.lata.velha.ordemDeServico.domain.entities.Proprietario;
import br.com.lata.velha.ordemDeServico.domain.repositories.ProprietarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DesativarProprietarioUseCase {

    private final ProprietarioRepository repository;

    public void execute(Long id) {
        Proprietario proprietario = repository.findActiveById(id);
        proprietario.deactivate();
        repository.save(proprietario);
    }
}