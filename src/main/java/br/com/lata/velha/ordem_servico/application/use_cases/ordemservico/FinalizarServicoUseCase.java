package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;

import java.util.stream.Collectors;

public class FinalizarServicoUseCase {

    private final FinalizarServicoGateway gateway;
    private final NotificarOrdemServicoService notificarService;
    private final Logger logger;

    public FinalizarServicoUseCase(FinalizarServicoGateway gateway,
                                   NotificarOrdemServicoService notificarService,
                                   Logger logger) {
        this.gateway = gateway;
        this.notificarService = notificarService;
        this.logger = logger;
    }

    public void execute(Input input) {
        logger.logInfo("Buscando OS e mecânico para finalizar execução de serviço - osId={}, servicoId={}", input.osId(), input.servicoId());
        var ordemServico = gateway.getOrdemServicoComServicosEPecas(input.osId());
        var mecanico = gateway.getFuncionarioPorUserId(input.userId());
        ordemServico.finalizarExecucaoServico(input.servicoId(), mecanico.getId());

        logger.logInfo("Retirando peças utilizadas do estoque - osId={}, servicoId={}", input.osId(), input.servicoId());
        retirarEstoques(ordemServico, input.servicoId());

        logger.logInfo("Salvando finalização de execução de serviço - osId={}, servicoId={}", input.osId(), input.servicoId());
        var saved = gateway.salvarOrdemServico(ordemServico);
        if (ordemServico.isFinalizada()) {
            logger.logInfo("Ordem de serviço finalizada - osId={}", saved.getId());
            notificarService.execute(saved);
        }
        logger.logInfo("Execução de serviço finalizada com sucesso - osId={}, servicoId={}", saved.getId(), input.servicoId());
    }

    private void retirarEstoques(OrdemServico ordemServico, Long servicoId) {
        var execucaoFinalizada = ordemServico.getExecucaoById(servicoId);
        var quantidadesMap = execucaoFinalizada.getPecas().stream()
                .collect(Collectors.toMap(
                        PecaAlocada::getPecaId,
                        PecaAlocada::getQuantidadeSolicitada
                ));
        var estoques = gateway.getEstoquePorPecaIds(quantidadesMap.keySet());
        if (estoques.size() != quantidadesMap.size()) {
            logger.logWarn("Finalização de serviço rejeitada: peças da execução sem registro de estoque - osId={}, servicoId={}, quantidadePecas={}, quantidadeEstoques={}",
                    ordemServico.getId(), servicoId, quantidadesMap.size(), estoques.size());
            throw new IllegalStateException("Nem todas as peças da execução possuem registro de estoque");
        }
        estoques.forEach(estoque -> estoque.retirar(quantidadesMap.get(estoque.getPecaId())));
        gateway.salvarEstoques(estoques);
    }

    public record Input(Long osId, Long servicoId, UserId userId) {}
}
