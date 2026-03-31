package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.infrastructure.persistence.entity.FuncionarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FuncionarioJpaRepository extends JpaRepository<FuncionarioEntity, Long> {

    Optional<FuncionarioEntity> findByUsername(String username);

    List<FuncionarioEntity> findByAtivoTrue();
}