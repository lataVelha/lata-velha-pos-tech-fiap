package br.com.lata.velha.domain.repository;

import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.domain.model.Peca;

import java.util.List;

public interface PecaRepository {

    Peca save(Peca peca);

    Peca findActiveById(Long id);

    List<Peca> findAllActive();

    PaginatedResult<Peca> findAllActivePaginated(int page, int size);
}
