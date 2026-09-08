package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.view.TempoPorEstado;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.entities.HistoricoEstadoOsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface HistoricoEstadoOsJpaRepository extends JpaRepository<HistoricoEstadoOsEntity, Long> {

    @Query("""
            SELECT new br.com.lata.velha.ordem_servico.domain.view(
                h.estadoOs,
                SUM(FUNCTION('TIMESTAMPDIFF', SECOND, h.dataInicio, COALESCE(h.dataFim, :agora)))
            )
            FROM HistoricoEstadoOsEntity h
            WHERE h.dataInicio >= :inicio AND h.dataInicio <= :fim
            GROUP BY h.estadoOs
            """)
    List<TempoPorEstado> obterTempoAgrupado(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("agora") LocalDateTime agora);

}
