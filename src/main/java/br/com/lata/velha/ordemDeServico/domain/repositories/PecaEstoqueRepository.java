package br.com.lata.velha.ordemDeServico.domain.repositories;

import br.com.lata.velha.ordemDeServico.domain.entities.PecaEstoque;

public interface PecaEstoqueRepository {

    PecaEstoque save(PecaEstoque pecaEstoque);

    PecaEstoque findByPecaId(Long pecaId);
}
