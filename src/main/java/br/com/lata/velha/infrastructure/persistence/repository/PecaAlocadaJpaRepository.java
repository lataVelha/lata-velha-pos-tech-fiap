package br.com.lata.velha.infrastructure.persistence.repository;

import br.com.lata.velha.infrastructure.persistence.entity.PecaAlocadaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PecaAlocadaJpaRepository extends JpaRepository<PecaAlocadaEntity, Long> {
    Page<PecaAlocadaEntity> findByServicoOSId(Long servicoOsId, Pageable pageable);

    @Query("""
                select coalesce(sum(p.quantidadeReservada),0)
                from PecaAlocadaEntity p
                where p.peca.id = :pecaId
                  and p.status in ('RESERVADA','PARCIAL')
            """)
    Integer somarQuantidadeReservadaPorPeca(Long pecaId);

    @Query("""
                select p
                from PecaAlocadaEntity p
                where p.peca.id = :pecaId
                  and p.status in ('PARCIAL','ENCOMENDADA')
                order by p.atualizado asc
            """)
    List<PecaAlocadaEntity> buscarPendentesPorPecaOrdenado(Long pecaId);
}
