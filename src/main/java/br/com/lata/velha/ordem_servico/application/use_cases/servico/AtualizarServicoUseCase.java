package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.ordem_servico.domain.repositories.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AtualizarServicoUseCase {

    private final ServicoRepository repository;

    public ServicoResponse execute(Long id, AtualizarServicoRequest request) {
        Servico servico = repository.getActiveById(id);
        servico.atualizar(request.nome(), request.descricao());
        return ServicoResponse.from(repository.save(servico));
    }
}
