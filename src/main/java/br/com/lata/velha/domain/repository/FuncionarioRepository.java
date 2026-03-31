package br.com.lata.velha.domain.repository;

import br.com.lata.velha.domain.model.Funcionario;
import java.util.List;
import java.util.Optional;

public interface FuncionarioRepository {

    Funcionario findByUsername(String username);
    Funcionario save(Funcionario funcionario);
    Optional<Funcionario> findById(Long id);
    List<Funcionario> findAll();
}