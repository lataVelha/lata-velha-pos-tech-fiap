package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.HistoricoEstadoOs;
import br.com.lata.velha.ordem_servico.domain.repositories.HistoricoEstadoOsRepository;
import br.com.lata.velha.ordem_servico.domain.view.TempoPorEstado;
import br.com.lata.velha.ordem_servico.infrastructure.persistence.mappers.HistoricoEstadoOsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HistoricoEstadoOsRepositoryImpl implements HistoricoEstadoOsRepository {

    private final HistoricoEstadoOsJpaRepository historicoEstadoOsJpaRepository;
    private final HistoricoEstadoOsMapper historicoEstadoOsMapper;

    @Override
    public void salvar(HistoricoEstadoOs historico) {
        historicoEstadoOsJpaRepository.save(historicoEstadoOsMapper.toEntity(historico));
    }

    @Override
    public List<TempoPorEstado> buscarTempoAgrupadoPorEstado(LocalDateTime inicio, LocalDateTime fim, LocalDateTime agora) {
        return historicoEstadoOsJpaRepository.obterTempoAgrupado(inicio, fim, agora);
    }
}
