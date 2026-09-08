package br.com.lata.velha.ordem_servico.domain.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.HistoricoEstadoOs;
import br.com.lata.velha.ordem_servico.domain.view.TempoPorEstado;

import java.time.LocalDateTime;
import java.util.List;

public interface HistoricoEstadoOsRepository {

    void salvar(HistoricoEstadoOs historico);
    List<TempoPorEstado> buscarTempoAgrupadoPorEstado(LocalDateTime inicio, LocalDateTime fim, LocalDateTime agora);
}
