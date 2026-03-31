package br.com.lata.velha.domain.repository;

import br.com.lata.velha.domain.model.Cargo;
import java.util.Optional;

public interface CargoRepository {
    Optional<Cargo> findById(Long id);
}