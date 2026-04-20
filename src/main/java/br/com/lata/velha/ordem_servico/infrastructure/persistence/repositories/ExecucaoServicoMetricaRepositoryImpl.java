package br.com.lata.velha.ordem_servico.infrastructure.persistence.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.TempoMedioExecucaoPorServico;
import br.com.lata.velha.ordem_servico.domain.repositories.ExecucaoServicoMetricaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ExecucaoServicoMetricaRepositoryImpl implements ExecucaoServicoMetricaRepository {

    private final ExecucaoServicoJpaRepository execucaoServicoJpaRepository;

    @Override
    public List<TempoMedioExecucaoPorServico> buscarTempoMedioExecucaoServicosFinalizados(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return execucaoServicoJpaRepository.buscarTempoMedioExecucaoServicosFinalizados(dataInicio, dataFim)
                .stream()
                .map(item -> new TempoMedioExecucaoPorServico(
                        item.getServicoId(),
                        item.getServicoNome(),
                        item.getTempoMedioMinutos()
                ))
                .toList();
    }
}
