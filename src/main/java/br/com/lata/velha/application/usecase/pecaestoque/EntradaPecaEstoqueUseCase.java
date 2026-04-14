package br.com.lata.velha.application.usecase.pecaestoque;

import br.com.lata.velha.application.assembler.PecaEstoqueAssembler;
import br.com.lata.velha.application.dto.request.MovimentarPecaEstoqueRequest;
import br.com.lata.velha.application.dto.response.PecaEstoqueResponse;
import br.com.lata.velha.domain.model.PecaEstoque;
import br.com.lata.velha.domain.repository.PecaAlocadaRepository;
import br.com.lata.velha.domain.repository.PecaEstoqueRepository;
import br.com.lata.velha.domain.repository.PecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EntradaPecaEstoqueUseCase {

    private final PecaRepository pecaRepository;
    private final PecaEstoqueRepository pecaEstoqueRepository;
    private final PecaAlocadaRepository pecaAlocadaRepository;
    private final PecaEstoqueAssembler assembler;

    @Transactional
    public PecaEstoqueResponse execute(Long pecaId, MovimentarPecaEstoqueRequest request) {

        pecaRepository.findActiveById(pecaId);

        PecaEstoque estoque = pecaEstoqueRepository.findByPecaId(pecaId);
        if (estoque == null) {
            estoque = new PecaEstoque(pecaId, 0);
        }

        estoque.adicionar(request.quantidade());

        movimentarReservasPendentes(pecaId, estoque);

        PecaEstoque saved = pecaEstoqueRepository.save(estoque);

        return assembler.toResponse(saved);
    }

    private void movimentarReservasPendentes(Long pecaId, PecaEstoque estoque) {

        int disponivel = estoque.getQuantidadeArmazenada();

        var pendentes =
                pecaAlocadaRepository
                        .buscarPendentesPorPecaOrdenado(pecaId);

        for (var pecaAlocada : pendentes) {

            if (disponivel <= 0) break;

            if (pecaAlocada.totalmenteReservada()) {
                continue;
            }

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