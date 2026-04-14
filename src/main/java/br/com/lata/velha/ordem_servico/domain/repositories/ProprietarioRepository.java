package br.com.lata.velha.ordem_servico.domain.repositories;

import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;

import java.util.List;

public interface ProprietarioRepository {

    Proprietario save(Proprietario proprietario);

    Proprietario findActiveById(Long id);

    Proprietario findActiveByDocumento(String documento);

    Proprietario findInactiveById(Long id);

    List<Proprietario> findAllActive();

    PaginatedResult<Proprietario> findAllActivePaginated(int page, int size);

    boolean existsByDocumento(String documento);
}