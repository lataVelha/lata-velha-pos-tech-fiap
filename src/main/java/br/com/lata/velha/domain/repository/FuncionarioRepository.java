package br.com.lata.velha.domain.repository;

import br.com.lata.velha.domain.model.Funcionario;

import java.util.Optional;

public interface FuncionarioRepository {

    Funcionario save(Funcionario funcionario);

    Optional<Funcionario> findById(Long id);

    Funcionario getById(Long id);
}