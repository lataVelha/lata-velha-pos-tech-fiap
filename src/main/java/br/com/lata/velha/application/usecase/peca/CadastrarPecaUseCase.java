package br.com.lata.velha.application.usecase.peca;

import br.com.lata.velha.application.assembler.PecaAssembler;
import br.com.lata.velha.application.dto.request.CadastrarPecaRequest;
import br.com.lata.velha.application.dto.response.PecaResponse;
import br.com.lata.velha.domain.model.Peca;
import br.com.lata.velha.domain.repository.PecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CadastrarPecaUseCase {

    private final PecaRepository repository;
    private final PecaAssembler assembler;

    public PecaResponse execute(CadastrarPecaRequest request) {
        Peca peca = assembler.toDomain(request);
        Peca saved = repository.save(peca);
        return assembler.toResponse(saved);
    }
}
