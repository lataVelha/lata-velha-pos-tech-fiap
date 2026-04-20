package br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque;

import br.com.lata.velha.ordem_servico.application.dtos.request.AjustarPecaEstoqueRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaEstoqueResponse;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.PecaNotFoundException;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AjustarPecaEstoqueUseCase {

    private final PecaRepository pecaRepository;
    private final PecaEstoqueRepository pecaEstoqueRepository;

    public PecaEstoqueResponse execute(Long pecaId, AjustarPecaEstoqueRequest request) {
        if(!pecaRepository.existsActiveById(pecaId))
            throw PecaNotFoundException.fromId(pecaId);

        var estoque = pecaEstoqueRepository.findByPecaId(pecaId)
                .orElse(PecaEstoque.create(pecaId));
        estoque.ajustar(request.quantidadeArmazenada(), request.quantidadeDisponivel());
        PecaEstoque saved = pecaEstoqueRepository.save(estoque);

        return PecaEstoqueResponse.from(saved);
    }
}
