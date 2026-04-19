package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.application.dtos.request.ProprietarioRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AtualizarProprietarioUseCase {

    private final ProprietarioRepository repository;

    public ProprietarioResponse execute(Long id, ProprietarioRequest request) {
        Proprietario existing = repository.findActiveById(id);
        request.updateDomain(existing);
        return ProprietarioResponse.from(repository.save(existing));
    }
}
