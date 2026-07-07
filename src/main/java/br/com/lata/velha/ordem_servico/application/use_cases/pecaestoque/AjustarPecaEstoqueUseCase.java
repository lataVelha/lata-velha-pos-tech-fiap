package br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque;

import br.com.lata.velha.ordem_servico.application.dtos.request.AjustarPecaEstoqueRequest;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;

public class AjustarPecaEstoqueUseCase {

    private final AjustarPecaEstoqueGateway gateway;

    public AjustarPecaEstoqueUseCase(AjustarPecaEstoqueGateway gateway) {
        this.gateway = gateway;
    }

    public PecaEstoque execute(Long pecaId, AjustarPecaEstoqueRequest request) {
        gateway.getPecaAtivaPorId(pecaId);
        var estoque = gateway.getEstoquePorPecaId(pecaId);
        estoque.ajustar(request.quantidadeArmazenada(), request.quantidadeDisponivel());
        return gateway.salvarEstoque(estoque);
    }
}
