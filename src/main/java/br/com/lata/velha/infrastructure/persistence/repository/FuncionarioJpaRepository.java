package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.infrastructure.persistence.entity.FuncionarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionarioJpaRepository extends JpaRepository<FuncionarioEntity, Long> {
}