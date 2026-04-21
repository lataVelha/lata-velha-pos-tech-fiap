package br.com.lata.velha.ordem_servico.domain.repositories;

import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;

public interface ServicoRepository {

    Servico save(Servico servico);

    Servico getActiveById(Long id);

    PaginatedResult<Servico> findAllActivePaginated(int page, int size);
}
