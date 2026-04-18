package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.PecaEstoqueEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PecaEstoqueJpaRepository extends JpaRepository<PecaEstoqueEntity, Long> {

    @Modifying
    @Transactional
    @Query("""
                UPDATE PecaEstoqueEntity p
                   SET p.quantidadeArmazenada = p.quantidadeArmazenada - :quantidade
                 WHERE p.pecaId = :pecaId
            """)
    void baixarEstoque(@Param("pecaId") Long pecaId,
                       @Param("quantidade") Integer quantidade);
}