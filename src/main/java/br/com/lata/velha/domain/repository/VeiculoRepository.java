package br.com.lata.velha.domain.repository;

import br.com.lata.velha.domain.common.PaginatedResult;
import br.com.lata.velha.domain.model.Veiculo;

import java.util.List;

public interface VeiculoRepository {

    Veiculo save(Veiculo veiculo);

    Veiculo findById(Long id);

    Veiculo findByPlaca(String placa);

    List<Veiculo> findByProprietarioId(Long proprietarioId);

    List<Veiculo> findAll();

    PaginatedResult<Veiculo> findAllPaginated(int page, int size);

    void deleteById(Long id);

    boolean existsByPlaca(String placa);
}