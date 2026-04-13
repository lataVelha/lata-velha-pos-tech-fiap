package br.com.lata.velha.ordemDeServico.domain.repositories;

import br.com.lata.velha.ordemDeServico.domain.entities.Cargo;
import java.util.Optional;

public interface CargoRepository {
    Cargo getById(Long id);
    Cargo getByIdWithRoles(Long id);
    Optional<Cargo> findById(Long id);
}