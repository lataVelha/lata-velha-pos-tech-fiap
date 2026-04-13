package br.com.lata.velha.domain.repository;

import br.com.lata.velha.domain.entities.PecaEstoque;

public interface PecaEstoqueRepository {

    PecaEstoque save(PecaEstoque pecaEstoque);

    PecaEstoque findByPecaId(Long pecaId);
}
