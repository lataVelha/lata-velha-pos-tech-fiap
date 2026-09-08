package br.com.lata.velha.ordem_servico.application.use_cases.historicoestadoos;

import br.com.lata.velha.ordem_servico.domain.view.TempoPorEstado;
import br.com.lata.velha.shared.application.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;

public class BuscarHistoricoEstadoUseCase {

    private final BuscarHistoricoEstadoOsGateway buscarHistoricoEstadoOsGateway;
    private final Logger logger;

    public BuscarHistoricoEstadoUseCase(BuscarHistoricoEstadoOsGateway buscarHistoricoEstadoOsGateway, Logger logger) {
        this.buscarHistoricoEstadoOsGateway = buscarHistoricoEstadoOsGateway;
        this.logger = logger;
    }

    public List<TempoPorEstado> execute(Input input) {
        logger.logInfo("Buscando histórico de estado da OS - inicio={}, fim={}", input.inicio, input.fim);
        LocalDateTime dataInicio = (input.inicio != null) ? input.inicio : LocalDateTime.now().minusDays(30);
        LocalDateTime dataFim = (input.fim != null) ? input.fim : LocalDateTime.now();
        return buscarHistoricoEstadoOsGateway.buscarTempoAgrupadoPorEstado(dataInicio, dataFim, LocalDateTime.now());
    }

    public record Input(LocalDateTime inicio, LocalDateTime fim) {
    }
}
