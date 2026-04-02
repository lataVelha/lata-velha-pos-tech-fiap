package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.infrastructure.persistence.entity.OrdemServicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdemServicoJpaRepository extends JpaRepository<OrdemServicoEntity, Long> {
}