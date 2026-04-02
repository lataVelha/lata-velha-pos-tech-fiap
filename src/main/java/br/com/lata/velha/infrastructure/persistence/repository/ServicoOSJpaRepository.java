package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.infrastructure.persistence.entity.ServicoOSEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoOSJpaRepository extends JpaRepository<ServicoOSEntity, Long> {
}