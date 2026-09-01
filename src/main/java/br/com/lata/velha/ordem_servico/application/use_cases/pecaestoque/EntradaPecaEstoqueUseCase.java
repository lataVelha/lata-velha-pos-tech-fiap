package br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque;

import br.com.lata.velha.ordem_servico.application.dtos.request.MovimentarPecaEstoqueRequest;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.shared.application.logging.Logger;

public class EntradaPecaEstoqueUseCase {

    private final EntradaPecaEstoqueGateway gateway;
    private final Logger logger;

    public EntradaPecaEstoqueUseCase(EntradaPecaEstoqueGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public PecaEstoque execute(Long pecaId, MovimentarPecaEstoqueRequest request) {
        logger.logInfo("Registrando entrada de estoque - pecaId={}, quantidade={}", pecaId, request.quantidade());
        gateway.getPecaAtivaPorId(pecaId);
        var estoque = gateway.getEstoquePorPecaId(pecaId);
        estoque.adicionar(request.quantidade());

        movimentarReservasPendentes(pecaId, estoque);

        logger.logInfo("Salvando estoque atualizado - pecaId={}", pecaId);
        var saved = gateway.salvarEstoque(estoque);
        logger.logInfo("Entrada de estoque registrada com sucesso - pecaId={}, quantidadeArmazenada={}, quantidadeDisponivel={}",
                pecaId, saved.getQuantidadeArmazenada(), saved.getQuantidadeDisponivel());
        return saved;
    }

    private void movimentarReservasPendentes(Long pecaId, PecaEstoque estoque) {
        var pendentes = gateway.getPecasAlocadasPendentes(pecaId);
        if (pendentes.isEmpty()) {
            logger.logDebug("Nenhuma peça alocada pendente para reserva - pecaId={}", pecaId);
            return;
        }

        logger.logDebug("Processando reservas de peças alocadas pendentes - pecaId={}, totalPendentes={}", pecaId, pendentes.size());
        for (var pecaAlocada : pendentes) {
            if (estoque.getQuantidadeDisponivel() <= 0) break;
            var execucao = gateway.getExecucaoServicoPorId(pecaAlocada.getExecucaoServicoId());
            execucao.reservarPeca(estoque);
            gateway.salvarExecucaoServico(execucao);
        }
    }
}
