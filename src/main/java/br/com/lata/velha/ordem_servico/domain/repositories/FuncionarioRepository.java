package br.com.lata.velha.ordem_servico.domain.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FuncionarioRepository {

    Funcionario save(Funcionario funcionario);

    Optional<Funcionario> findById(Long id);

    Funcionario getById(Long id);

    Funcionario getByUserId(UUID userId);

    List<Funcionario> findAllByCargoNome(String cargoNome);
}