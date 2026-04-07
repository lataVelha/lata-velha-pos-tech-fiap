package br.com.lata.velha.application.usecase.pecaestoque;

import br.com.lata.velha.application.assembler.PecaEstoqueAssembler;
import br.com.lata.velha.application.dto.request.MovimentarPecaEstoqueRequest;
import br.com.lata.velha.application.dto.response.PecaEstoqueResponse;
import br.com.lata.velha.domain.repository.PecaEstoqueRepository;
import br.com.lata.velha.domain.repository.PecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaidaPecaEstoqueUseCase {

    private final PecaRepository pecaRepository;
    private final PecaEstoqueRepository pecaEstoqueRepository;
    private final PecaEstoqueAssembler assembler;

    public PecaEstoqueResponse execute(Long pecaId, MovimentarPecaEstoqueRequest request) {
        pecaRepository.findActiveById(pecaId);

        var estoque = pecaEstoqueRepository.findByPecaId(pecaId);
        if (estoque == null) {
            throw new IllegalArgumentException("Estoque da peça não encontrado");
        }

        estoque.remover(request.quantidade());
        var saved = pecaEstoqueRepository.save(estoque);

        return assembler.toResponse(saved);
    }
}
