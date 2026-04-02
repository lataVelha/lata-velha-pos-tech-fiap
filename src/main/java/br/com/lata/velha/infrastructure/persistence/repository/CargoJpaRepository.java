package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.infrastructure.persistence.entity.CargoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CargoJpaRepository extends JpaRepository<CargoEntity, Long> {
}