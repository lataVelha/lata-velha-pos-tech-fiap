package br.com.lata.velha.application.usecase.pecaestoque;

import br.com.lata.velha.application.assembler.PecaEstoqueAssembler;
import br.com.lata.velha.application.dto.request.AjustarPecaEstoqueRequest;
import br.com.lata.velha.application.dto.response.PecaEstoqueResponse;
import br.com.lata.velha.domain.model.PecaEstoque;
import br.com.lata.velha.domain.repository.PecaEstoqueRepository;
import br.com.lata.velha.domain.repository.PecaRepository;
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
