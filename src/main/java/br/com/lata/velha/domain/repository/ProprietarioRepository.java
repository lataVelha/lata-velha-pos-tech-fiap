package br.com.lata.velha.domain.repository;

import br.com.lata.velha.domain.common.PaginatedResult;
import br.com.lata.velha.domain.model.Proprietario;

import java.util.List;

public interface ProprietarioRepository {

    Proprietario save(Proprietario proprietario);

    Proprietario findById(Long id);

    Proprietario findByDocumento(String documento);

    List<Proprietario> findAll();

    PaginatedResult<Proprietario> findAllPaginated(int page, int size);

    void deleteById(Long id);

    boolean existsByDocumento(String documento);
}