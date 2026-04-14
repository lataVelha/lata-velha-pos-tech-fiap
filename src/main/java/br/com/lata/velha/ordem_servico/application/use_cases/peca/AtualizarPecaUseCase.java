package br.com.lata.velha.ordem_servico.application.use_cases.peca;

import br.com.lata.velha.ordem_servico.application.assemblers.PecaAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarPecaRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
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
