package br.com.lata.velha.domain.repository;

import br.com.lata.velha.domain.model.Funcionario;

public interface FuncionarioRepository {

    Funcionario findByUsername(String username);

    Funcionario findById(Long id);
}