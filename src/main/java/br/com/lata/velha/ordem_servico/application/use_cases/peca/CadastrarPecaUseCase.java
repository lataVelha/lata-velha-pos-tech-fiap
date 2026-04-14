package br.com.lata.velha.ordem_servico.application.use_cases.peca;

import br.com.lata.velha.ordem_servico.application.assemblers.PecaAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.request.CadastrarPecaRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
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
