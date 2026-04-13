package br.com.lata.velha.application.usecase.proprietario;

import br.com.lata.velha.domain.entities.Proprietario;
import br.com.lata.velha.domain.repository.ProprietarioRepository;
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