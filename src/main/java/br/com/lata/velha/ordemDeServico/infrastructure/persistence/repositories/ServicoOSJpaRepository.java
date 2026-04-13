package br.com.lata.velha.ordemDeServico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordemDeServico.infrastructure.persistence.entities.ServicoOSEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoOSJpaRepository extends JpaRepository<ServicoOSEntity, Long> {
}