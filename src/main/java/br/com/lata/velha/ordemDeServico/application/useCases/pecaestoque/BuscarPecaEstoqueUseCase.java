package br.com.lata.velha.ordemDeServico.application.useCases.pecaestoque;

import br.com.lata.velha.ordemDeServico.application.assemblers.PecaEstoqueAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.response.PecaEstoqueResponse;
import br.com.lata.velha.ordemDeServico.domain.repositories.PecaEstoqueRepository;
import br.com.lata.velha.ordemDeServico.domain.repositories.PecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuscarPecaEstoqueUseCase {

    private final PecaRepository pecaRepository;
    private final PecaEstoqueRepository pecaEstoqueRepository;
    private final PecaEstoqueAssembler assembler;

    public PecaEstoqueResponse execute(Long pecaId) {
        pecaRepository.findActiveById(pecaId);

        var estoque = pecaEstoqueRepository.findByPecaId(pecaId);
        if (estoque == null) {
            throw new IllegalArgumentException("Estoque da peça não encontrado");
        }

        return assembler.toResponse(estoque);
    }
}
