package br.com.lata.velha.ordemDeServico.application.useCases.servico;

import br.com.lata.velha.ordemDeServico.application.assemblers.ServicoAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.request.AtualizarServicoRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.ordemDeServico.domain.entities.Servico;
import br.com.lata.velha.ordemDeServico.domain.repositories.ServicoRepository;
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
