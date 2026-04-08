package br.com.lata.velha.application.usecase.pecaalocada;

import br.com.lata.velha.domain.repository.PecaAlocadaRepository;
import br.com.lata.velha.domain.repository.PecaEstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverPecaAlocadaUseCase {

    private final PecaAlocadaRepository pecaAlocadaRepository;
    private final PecaEstoqueRepository pecaEstoqueRepository;

    @Transactional
    public void execute(Long id) {
        var pecaAlocada = pecaAlocadaRepository.findById(id);
        var estoque = pecaEstoqueRepository.findByPecaId(pecaAlocada.getPecaId());

        if (estoque == null) {
            throw new IllegalArgumentException("Estoque da peça não encontrado");
        }

        estoque.adicionar(pecaAlocada.getQuantidadeAlocada());
        pecaEstoqueRepository.save(estoque);

        pecaAlocadaRepository.delete(id);
    }
}