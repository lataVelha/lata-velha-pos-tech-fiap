package br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque;

import br.com.lata.velha.ordem_servico.application.dtos.request.MovimentarPecaEstoqueRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaEstoqueResponse;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaidaPecaEstoqueUseCase {

    private final PecaRepository pecaRepository;
    private final PecaEstoqueRepository pecaEstoqueRepository;

    public PecaEstoqueResponse execute(Long pecaId, MovimentarPecaEstoqueRequest request) {
        pecaRepository.getActiveById(pecaId);

        var estoque = pecaEstoqueRepository.findByPecaId(pecaId)
                .orElseThrow(() -> new IllegalArgumentException("Estoque da peça não encontrado"));

        estoque.retirar(request.quantidade());
        var saved = pecaEstoqueRepository.save(estoque);
        return PecaEstoqueResponse.from(saved);
    }
}
