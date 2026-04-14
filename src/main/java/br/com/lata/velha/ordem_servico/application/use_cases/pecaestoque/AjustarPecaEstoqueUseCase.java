package br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque;

import br.com.lata.velha.ordem_servico.application.assemblers.PecaEstoqueAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.request.AjustarPecaEstoqueRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaEstoqueResponse;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AjustarPecaEstoqueUseCase {

    private final PecaRepository pecaRepository;
    private final PecaEstoqueRepository pecaEstoqueRepository;
    private final PecaEstoqueAssembler assembler;

    public PecaEstoqueResponse execute(Long pecaId, AjustarPecaEstoqueRequest request) {
        pecaRepository.findActiveById(pecaId);

        PecaEstoque estoque = pecaEstoqueRepository.findByPecaId(pecaId);
        if (estoque == null) {
            estoque = new PecaEstoque(pecaId, 0);
        }

        estoque.ajustar(request.quantidadeArmazenada());
        PecaEstoque saved = pecaEstoqueRepository.save(estoque);

        return assembler.toResponse(saved);
    }
}
