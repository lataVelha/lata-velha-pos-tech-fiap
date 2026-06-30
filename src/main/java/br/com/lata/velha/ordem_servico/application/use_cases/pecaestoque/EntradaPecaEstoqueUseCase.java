package br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque;

import br.com.lata.velha.ordem_servico.application.dtos.request.MovimentarPecaEstoqueRequest;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;

public class EntradaPecaEstoqueUseCase {

    private final EntradaPecaEstoqueGateway gateway;

    public EntradaPecaEstoqueUseCase(EntradaPecaEstoqueGateway gateway) {
        this.gateway = gateway;
    }

    public PecaEstoque execute(Long pecaId, MovimentarPecaEstoqueRequest request) {
        gateway.getPecaAtivaPorId(pecaId);
        var estoque = gateway.getEstoquePorPecaId(pecaId);
        estoque.adicionar(request.quantidade());
        movimentarReservasPendentes(pecaId, estoque);
        return gateway.salvarEstoque(estoque);
    }

    private void movimentarReservasPendentes(Long pecaId, PecaEstoque estoque) {
        var pendentes = gateway.getPecasAlocadasPendentes(pecaId);
        if (pendentes.isEmpty()) return;

        for (var pecaAlocada : pendentes) {
            if (estoque.getQuantidadeDisponivel() <= 0) break;
            var execucao = gateway.getExecucaoServicoPorId(pecaAlocada.getExecucaoServicoId());
            execucao.reservarPeca(estoque);
            gateway.salvarExecucaoServico(execucao);
        }
    }
}
