package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.infrastructure.persistence.entity.ServicoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServicoJpaRepository extends JpaRepository<ServicoEntity, Long> {

    Optional<ServicoEntity> findByIdAndAtivoTrue(Long id);

    Page<ServicoEntity> findByAtivoTrue(Pageable pageable);
}
