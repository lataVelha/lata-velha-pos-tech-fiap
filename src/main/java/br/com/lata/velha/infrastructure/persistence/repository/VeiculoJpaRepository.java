package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.infrastructure.persistence.entity.VeiculoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VeiculoJpaRepository extends JpaRepository<VeiculoEntity, Long> {

    Optional<VeiculoEntity> findByPlaca(String placa);

    List<VeiculoEntity> findByProprietarioId(Long proprietarioId);

    boolean existsByPlaca(String placa);
}