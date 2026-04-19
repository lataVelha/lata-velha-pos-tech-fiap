package br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque;

import br.com.lata.velha.ordem_servico.application.dtos.request.MovimentarPecaEstoqueRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaEstoqueResponse;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
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
        pecaRepository.findActiveById(pecaId);

        PecaEstoque estoque = pecaEstoqueRepository.findByPecaId(pecaId);
        if (estoque == null) {
            estoque = new PecaEstoque(pecaId, 0);
        }

        estoque.adicionar(request.quantidade());
        movimentarReservasPendentes(pecaId, estoque);

        return PecaEstoqueResponse.from(pecaEstoqueRepository.save(estoque));
    }

    private void movimentarReservasPendentes(Long pecaId, PecaEstoque estoque) {
        int disponivel = estoque.getQuantidadeArmazenada();

        var pendentes = pecaAlocadaRepository.buscarPendentesPorPecaOrdenado(pecaId);

        for (var pecaAlocada : pendentes) {
            if (disponivel <= 0) break;
            if (pecaAlocada.totalmenteReservada()) continue;

            int faltante = pecaAlocada.getQuantidadeEncomendada();
            if (faltante <= 0) continue;

            int reservar = Math.min(disponivel, faltante);
            pecaAlocada.movimentarParaReservado(reservar);
            pecaAlocadaRepository.save(pecaAlocada);
            disponivel -= reservar;
        }

        estoque.setQuantidadeArmazenada(disponivel);
    }
}
