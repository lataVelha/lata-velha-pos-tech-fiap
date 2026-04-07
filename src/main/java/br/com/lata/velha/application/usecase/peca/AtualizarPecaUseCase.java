package br.com.lata.velha.application.usecase.peca;

import br.com.lata.velha.application.assembler.PecaAssembler;
import br.com.lata.velha.application.dto.request.AtualizarPecaRequest;
import br.com.lata.velha.application.dto.response.PecaResponse;
import br.com.lata.velha.domain.model.Peca;
import br.com.lata.velha.domain.repository.PecaRepository;
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
