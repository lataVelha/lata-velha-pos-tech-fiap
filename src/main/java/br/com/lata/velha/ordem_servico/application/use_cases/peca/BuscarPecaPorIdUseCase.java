package br.com.lata.velha.ordem_servico.application.use_cases.peca;

import br.com.lata.velha.ordem_servico.application.dtos.response.PecaResponse;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuscarPecaPorIdUseCase {

    private final PecaRepository repository;

    public PecaResponse execute(Long id) {
        return PecaResponse.from(repository.findActiveById(id));
    }
}
