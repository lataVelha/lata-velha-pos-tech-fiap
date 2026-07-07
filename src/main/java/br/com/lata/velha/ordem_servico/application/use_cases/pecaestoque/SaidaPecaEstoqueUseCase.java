package br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque;

import br.com.lata.velha.ordem_servico.application.dtos.request.MovimentarPecaEstoqueRequest;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;

public class SaidaPecaEstoqueUseCase {

    private final SaidaPecaEstoqueGateway gateway;

    public SaidaPecaEstoqueUseCase(SaidaPecaEstoqueGateway gateway) {
        this.gateway = gateway;
    }

    public PecaEstoque execute(Long pecaId, MovimentarPecaEstoqueRequest request) {
        gateway.getPecaAtivaPorId(pecaId);
        var estoque = gateway.getEstoquePorPecaId(pecaId);
        estoque.retirar(request.quantidade());
        return gateway.salvarEstoque(estoque);
    }
}
