package br.com.lata.velha.ordem_servico.application.use_cases.historicoestadoos;

import br.com.lata.velha.ordem_servico.domain.view.TempoPorEstado;

import java.time.LocalDateTime;
import java.util.List;

public interface BuscarHistoricoEstadoOsGateway {

    List<TempoPorEstado> buscarTempoAgrupadoPorEstado(LocalDateTime inicio, LocalDateTime fim, LocalDateTime agora);
}
