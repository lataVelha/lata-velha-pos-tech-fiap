package br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque;

import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;

public class BuscarPecaEstoqueUseCase {

    private final BuscarPecaEstoqueGateway gateway;

    public BuscarPecaEstoqueUseCase(BuscarPecaEstoqueGateway gateway) {
        this.gateway = gateway;
    }

    public PecaEstoque execute(Long pecaId) {
        gateway.getPecaAtivaPorId(pecaId);
        return gateway.getEstoquePorPecaId(pecaId);
    }
}
