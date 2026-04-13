package br.com.lata.velha.ordemDeServico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordemDeServico.infrastructure.persistence.entities.FuncionarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionarioJpaRepository extends JpaRepository<FuncionarioEntity, Long> {
}