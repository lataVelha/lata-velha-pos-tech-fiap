package br.com.lata.velha.ordemDeServico.application.useCases.peca;

import br.com.lata.velha.ordemDeServico.application.assemblers.PecaAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.request.AtualizarPecaRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.response.PecaResponse;
import br.com.lata.velha.ordemDeServico.domain.entities.Peca;
import br.com.lata.velha.ordemDeServico.domain.repositories.PecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AtualizarPecaUseCase {

    private final PecaRepository repository;
    private final PecaAssembler assembler;

    public PecaResponse execute(Long id, AtualizarPecaRequest request) {
        Peca peca = repository.findActiveById(id);

        peca.atualizar(request.nome(), request.descricao(), request.valor());

        Peca saved = repository.save(peca);
        return assembler.toResponse(saved);
    }
}
