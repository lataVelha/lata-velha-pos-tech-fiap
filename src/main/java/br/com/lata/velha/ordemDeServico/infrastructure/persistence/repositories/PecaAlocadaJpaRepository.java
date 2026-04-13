package br.com.lata.velha.ordemDeServico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordemDeServico.infrastructure.persistence.entities.PecaAlocadaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PecaAlocadaJpaRepository extends JpaRepository<PecaAlocadaEntity, Long> {
    Page<PecaAlocadaEntity> findByServicoOSId(Long servicoOsId, Pageable pageable);
}
