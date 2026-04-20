package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.ordem_servico.domain.repositories.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuscarServicoPorIdUseCase {

    private final ServicoRepository repository;

    public ServicoResponse execute(Long id) {
        return ServicoResponse.from(repository.findActiveById(id));
    }
}
