package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.PecaEstoqueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PecaEstoqueJpaRepository extends JpaRepository<PecaEstoqueEntity, Long> {
}
