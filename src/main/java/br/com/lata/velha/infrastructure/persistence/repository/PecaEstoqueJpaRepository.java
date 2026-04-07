package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.infrastructure.persistence.entity.PecaEstoqueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PecaEstoqueJpaRepository extends JpaRepository<PecaEstoqueEntity, Long> {
}
