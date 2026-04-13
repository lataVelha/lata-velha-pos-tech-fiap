package br.com.lata.velha.domain.repository;

import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.domain.entities.Servico;

public interface ServicoRepository {

    Servico save(Servico servico);

    Servico findActiveById(Long id);

    PaginatedResult<Servico> findAllActivePaginated(int page, int size);
}
