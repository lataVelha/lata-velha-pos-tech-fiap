package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.FuncionarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FuncionarioJpaRepository extends JpaRepository<FuncionarioEntity, Long> {
    Optional<FuncionarioEntity> findByUserId(UUID userId);
}