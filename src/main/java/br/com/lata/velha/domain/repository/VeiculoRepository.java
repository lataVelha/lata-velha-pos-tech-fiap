package br.com.lata.velha.domain.repository;

import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.domain.entities.Veiculo;

import java.util.List;

public interface VeiculoRepository {

    Veiculo save(Veiculo veiculo);

    Veiculo findActiveById(Long id);

    List<Veiculo> findActiveByProprietarioId(Long proprietarioId);

    List<Veiculo> findAllActive();

    PaginatedResult<Veiculo> findAllActivePaginated(int page, int size);
    
    Veiculo findInactiveById(Long id);

}