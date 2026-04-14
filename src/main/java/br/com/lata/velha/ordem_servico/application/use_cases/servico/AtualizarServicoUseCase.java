package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.application.assemblers.ServicoAssembler;
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
    private final ServicoAssembler assembler;

    public ServicoResponse execute(Long id, AtualizarServicoRequest request) {
        Servico servico = repository.findActiveById(id);

        servico.atualizar(request.nome(), request.descricao());

        Servico saved = repository.save(servico);
        return assembler.toResponse(saved);
    }
}
