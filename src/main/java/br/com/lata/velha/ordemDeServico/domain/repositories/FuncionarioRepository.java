package br.com.lata.velha.ordemDeServico.domain.repositories;

import br.com.lata.velha.ordemDeServico.domain.entities.Funcionario;

import java.util.Optional;

public interface FuncionarioRepository {

    Funcionario save(Funcionario funcionario);

    Optional<Funcionario> findById(Long id);

    Funcionario getById(Long id);
}