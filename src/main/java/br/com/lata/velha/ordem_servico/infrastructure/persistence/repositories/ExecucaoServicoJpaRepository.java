package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.ExecucaoServicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecucaoServicoJpaRepository extends JpaRepository<ExecucaoServicoEntity, Long> {
}