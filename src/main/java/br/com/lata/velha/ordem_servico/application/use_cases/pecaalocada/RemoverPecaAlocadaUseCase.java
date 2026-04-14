package br.com.lata.velha.ordem_servico.application.use_cases.pecaalocada;

import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaAlocadaRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverPecaAlocadaUseCase {

    private final PecaAlocadaRepository pecaAlocadaRepository;
    private final PecaEstoqueRepository pecaEstoqueRepository;

    @Transactional
    public void execute(Long pecaAlocadaId) {
        PecaAlocada pecaAlocada = pecaAlocadaRepository.findById(pecaAlocadaId);
        if (pecaAlocada == null) {
            throw new IllegalArgumentException("Peça alocada não encontrada");
        }

        var estoque = pecaEstoqueRepository.findByPecaId(pecaAlocada.getPecaId());
        if (estoque != null) {
            estoque.adicionar(pecaAlocada.getQuantidadeSolicitada());
            pecaEstoqueRepository.save(estoque);
        }

        pecaAlocadaRepository.delete(pecaAlocadaId);
    }
}
