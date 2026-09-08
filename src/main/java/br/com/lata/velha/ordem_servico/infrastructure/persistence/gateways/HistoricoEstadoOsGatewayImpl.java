package br.com.lata.velha.ordem_servico.infrastructure.persistence.gateways;

import br.com.lata.velha.ordem_servico.application.use_cases.historicoestadoos.BuscarHistoricoEstadoOsGateway;
import br.com.lata.velha.ordem_servico.application.use_cases.historicoestadoos.CadastrarHistoricoEstadoOsGateway;
import br.com.lata.velha.ordem_servico.domain.entities.HistoricoEstadoOs;
import br.com.lata.velha.ordem_servico.domain.repositories.HistoricoEstadoOsRepository;
import br.com.lata.velha.ordem_servico.domain.view.TempoPorEstado;
import br.com.lata.velha.shared.application.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HistoricoEstadoOsGatewayImpl implements BuscarHistoricoEstadoOsGateway, CadastrarHistoricoEstadoOsGateway {

    private final HistoricoEstadoOsRepository historicoEstadoOsRepository;
    private final Logger logger;

    @Override
    public List<TempoPorEstado> buscarTempoAgrupadoPorEstado(LocalDateTime inicio, LocalDateTime fim, LocalDateTime agora) {
        logger.logInfo("Buscando histórico de estado da OS - inicio={}, fim={}, agora={}", inicio, fim, agora);
        return historicoEstadoOsRepository.buscarTempoAgrupadoPorEstado(inicio, fim, agora);
    }

    @Override
    public void salvar(HistoricoEstadoOs historico) {
        logger.logInfo("Cadastrando histórico de estado da OS - osId={}, estado={}, dataInicio={}", historico.getOsId(), historico.getEstadoOs(), historico.getDataInicio());
        historicoEstadoOsRepository.salvar(historico);
    }
}
