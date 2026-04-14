package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.PecaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PecaJpaRepository extends JpaRepository<PecaEntity, Long> {

    Optional<PecaEntity> findByIdAndAtivoTrue(Long id);

    List<PecaEntity> findByAtivoTrue();

    Page<PecaEntity> findByAtivoTrue(Pageable pageable);
}
