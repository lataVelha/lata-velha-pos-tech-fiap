package br.com.lata.velha.domain.repository;

import br.com.lata.velha.domain.model.Cargo;
import java.util.Optional;

public interface CargoRepository {
    Cargo getById(Long id);
    Cargo getByIdWithRoles(Long id);
    Optional<Cargo> findById(Long id);
}