package br.com.lata.velha.domain.repository;

import br.com.lata.velha.domain.common.PaginatedResult;
import br.com.lata.velha.domain.model.Funcionario;
import java.util.List;

public interface FuncionarioRepository {

    Funcionario findByUsername(String username);

    Funcionario save(Funcionario funcionario);

    Funcionario findActiveById(Long id);

    List<Funcionario> findAllActive();

    PaginatedResult<Funcionario> findAllActivePaginated(int page, int size);

    Funcionario findById(Long id);
}