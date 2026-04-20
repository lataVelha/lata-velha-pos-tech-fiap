package br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque;

import br.com.lata.velha.ordem_servico.application.dtos.request.MovimentarPecaEstoqueRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaEstoqueResponse;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.PecaNotFoundException;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaAlocadaRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EntradaPecaEstoqueUseCase {

    private final PecaRepository pecaRepository;
    private final PecaEstoqueRepository pecaEstoqueRepository;
    private final PecaAlocadaRepository pecaAlocadaRepository;

    @Transactional
    public PecaEstoqueResponse execute(Long pecaId, MovimentarPecaEstoqueRequest request) {
        if(!pecaRepository.existsActiveById(pecaId))
            throw PecaNotFoundException.fromId(pecaId);

        var estoque = pecaEstoqueRepository.findByPecaId(pecaId)
                .orElse(PecaEstoque.create(pecaId));

        estoque.adicionar(request.quantidade());
        movimentarReservasPendentes(pecaId, estoque);

        PecaEstoque saved = pecaEstoqueRepository.save(estoque);
        return PecaEstoqueResponse.from(saved);
    }

    private void movimentarReservasPendentes(Long pecaId, PecaEstoque estoque) {
        var pendentes = pecaAlocadaRepository.buscarPendentesPorPecaOrdenado(pecaId);

        for (var pecaAlocada : pendentes) {
            if (estoque.getQuantidadeDisponivel() <= 0) break;
            if (pecaAlocada.totalmenteReservada()) continue;
            pecaAlocada.reservar(estoque);
            pecaAlocadaRepository.save(pecaAlocada);
        }
    }
}