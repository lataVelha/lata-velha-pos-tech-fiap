package br.com.lata.velha.ordem_servico.application.use_cases.pecaalocada;

import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarPecaAlocadaRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaAlocadaResponse;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaAlocadaRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AtualizarPecaAlocadaUseCase {

    private final PecaAlocadaRepository pecaAlocadaRepository;
    private final PecaEstoqueRepository pecaEstoqueRepository;

    @Transactional
    public PecaAlocadaResponse execute(Long pecaAlocadaId, AtualizarPecaAlocadaRequest request) {
        PecaAlocada pecaAlocada = pecaAlocadaRepository.findById(pecaAlocadaId);
        if (pecaAlocada == null) {
            throw new IllegalArgumentException("Peça alocada não encontrada");
        }

        int diff = request.quantidade() - pecaAlocada.getQuantidadeSolicitada();

        var estoque = pecaEstoqueRepository.findByPecaId(pecaAlocada.getPecaId());
        if (diff > 0) {
            estoque.remover(diff);
        } else if (diff < 0) {
            estoque.adicionar(-diff);
        }
        pecaEstoqueRepository.save(estoque);

        pecaAlocada.setQuantidadeSolicitada(request.quantidade());
        return PecaAlocadaResponse.from(pecaAlocadaRepository.save(pecaAlocada));
    }
}
