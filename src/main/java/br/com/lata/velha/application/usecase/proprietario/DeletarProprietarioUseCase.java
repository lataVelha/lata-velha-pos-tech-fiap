package br.com.lata.velha.application.usecase.proprietario;

import br.com.lata.velha.domain.repository.ProprietarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeletarProprietarioUseCase {

    private final ProprietarioRepository repository;

    public void execute(Long id) {
        repository.deleteById(id);
    }
}