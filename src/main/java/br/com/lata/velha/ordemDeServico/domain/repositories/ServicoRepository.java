package br.com.lata.velha.ordemDeServico.domain.repositories;

import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordemDeServico.domain.entities.Servico;

public interface ServicoRepository {

    Servico save(Servico servico);

    Servico findActiveById(Long id);

    PaginatedResult<Servico> findAllActivePaginated(int page, int size);
}
