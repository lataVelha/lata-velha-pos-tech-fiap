package br.com.lata.velha.ordem_servico.domain.repositories;

import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;

public interface PecaRepository {

    Peca save(Peca peca);

    Peca getActiveById(Long id);

    PaginatedResult<Peca> findAllActivePaginated(int page, int size);

    boolean existsActiveById(Long pecaId);
}
