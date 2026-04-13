package br.com.lata.velha.ordemDeServico.application.useCases.pecaestoque;

import br.com.lata.velha.ordemDeServico.application.assemblers.PecaEstoqueAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.request.MovimentarPecaEstoqueRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.response.PecaEstoqueResponse;
import br.com.lata.velha.ordemDeServico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordemDeServico.domain.repositories.PecaEstoqueRepository;
import br.com.lata.velha.ordemDeServico.domain.repositories.PecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntradaPecaEstoqueUseCase {

    private final PecaRepository pecaRepository;
    private final PecaEstoqueRepository pecaEstoqueRepository;
    private final PecaEstoqueAssembler assembler;

    public PecaEstoqueResponse execute(Long pecaId, MovimentarPecaEstoqueRequest request) {
        pecaRepository.findActiveById(pecaId);

        PecaEstoque estoque = pecaEstoqueRepository.findByPecaId(pecaId);
        if (estoque == null) {
            estoque = new PecaEstoque(pecaId, 0);
        }

        estoque.adicionar(request.quantidade());
        PecaEstoque saved = pecaEstoqueRepository.save(estoque);

        return assembler.toResponse(saved);
    }
}
