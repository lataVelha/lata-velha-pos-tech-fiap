package br.com.lata.velha.ordem_servico.domain.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;

public interface PecaEstoqueRepository {

    PecaEstoque save(PecaEstoque pecaEstoque);

    PecaEstoque findByPecaId(Long pecaId);

    void baixarEstoque(Long pecaId, Integer quantidade);
}
