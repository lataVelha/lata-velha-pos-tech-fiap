package br.com.lata.velha.ordem_servico.application.use_cases.historicoestadoos;

import br.com.lata.velha.ordem_servico.domain.entities.HistoricoEstadoOs;
import br.com.lata.velha.shared.application.logging.Logger;

public class CadastrarHistoricoEstadoOsUseCase {

    private final CadastrarHistoricoEstadoOsGateway gateway;
    private final Logger logger;

    public CadastrarHistoricoEstadoOsUseCase(CadastrarHistoricoEstadoOsGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public void execute(Input input) {
        logger.logInfo("Cadastrando histórico de estado da OS - osId={}, estado={}, dataInicio={}", input.historicoEstadoOs.getOsId(), input.historicoEstadoOs.getEstadoOs(), input.historicoEstadoOs.getDataInicio());
        gateway.salvar(input.historicoEstadoOs);
    }

    public record Input(
            HistoricoEstadoOs historicoEstadoOs
    ) {

    }
}
