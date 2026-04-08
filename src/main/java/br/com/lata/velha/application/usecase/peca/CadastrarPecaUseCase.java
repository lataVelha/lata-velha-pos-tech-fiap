package br.com.lata.velha.application.usecase.peca;

import br.com.lata.velha.application.assembler.PecaAssembler;
import br.com.lata.velha.application.dto.request.CadastrarPecaRequest;
import br.com.lata.velha.application.dto.response.PecaResponse;
import br.com.lata.velha.domain.model.Peca;
import br.com.lata.velha.domain.model.PecaEstoque;
import br.com.lata.velha.domain.repository.PecaEstoqueRepository;
import br.com.lata.velha.domain.repository.PecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CadastrarPecaUseCase {

    private final PecaRepository repository;
    private final PecaEstoqueRepository pecaEstoqueRepository;
    private final PecaAssembler assembler;

    @Transactional
    public PecaResponse execute(CadastrarPecaRequest request) {
        Peca peca = assembler.toDomain(request);
        Peca saved = repository.save(peca);
        pecaEstoqueRepository.save(new PecaEstoque(saved.getId(), 0));
        return assembler.toResponse(saved);
    }
}
