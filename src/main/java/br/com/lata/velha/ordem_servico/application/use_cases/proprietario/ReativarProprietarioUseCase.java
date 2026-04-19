package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReativarProprietarioUseCase {

    private final ProprietarioRepository repository;

    public ProprietarioResponse execute(Long id) {
        Proprietario proprietario = repository.findInactiveById(id);
        proprietario.activate();
        return ProprietarioResponse.from(repository.save(proprietario));
    }
}
