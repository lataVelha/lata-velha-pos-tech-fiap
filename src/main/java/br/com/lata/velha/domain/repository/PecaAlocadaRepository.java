package br.com.lata.velha.domain.repository;

import br.com.lata.velha.domain.common.PaginatedResult;
import br.com.lata.velha.domain.model.PecaAlocada;

import java.util.List;

public interface PecaAlocadaRepository {

    PecaAlocada save(PecaAlocada pecaAlocada);

    PecaAlocada findById(Long id);

    PaginatedResult<PecaAlocada> findByServicoOsId(Long servicoOsId, int page, int size);

    void delete(Long id);

    Integer somarQuantidadeReservadaPorPeca(Long pecaId);

    List<PecaAlocada> buscarPendentesPorPecaOrdenado(Long pecaId);
}