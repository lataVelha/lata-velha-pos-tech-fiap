package br.com.lata.velha.domain.repository;

import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.domain.model.PecaAlocada;

public interface PecaAlocadaRepository {

    PecaAlocada save(PecaAlocada pecaAlocada);

    PecaAlocada findById(Long id);

    PaginatedResult<PecaAlocada> findByServicoOsId(Long servicoOsId, int page, int size);

    void delete(Long id);
}