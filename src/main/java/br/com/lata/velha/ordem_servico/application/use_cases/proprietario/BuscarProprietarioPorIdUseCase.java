package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuscarProprietarioPorIdUseCase {

    private final ProprietarioRepository repository;

    public ProprietarioResponse execute(Long id) {
        return ProprietarioResponse.from(repository.getActiveById(id));
    }
}
