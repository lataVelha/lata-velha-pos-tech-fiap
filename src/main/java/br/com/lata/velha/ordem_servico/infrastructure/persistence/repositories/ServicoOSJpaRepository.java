package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ServicoOSEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoOSJpaRepository extends JpaRepository<ServicoOSEntity, Long> {
}