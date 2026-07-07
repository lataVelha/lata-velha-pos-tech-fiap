package br.com.lata.velha.ordem_servico.application.use_cases.peca;

import br.com.lata.velha.ordem_servico.application.dtos.request.CadastrarPecaRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;

public class CadastrarPecaUseCase {

    private final CadastrarPecaGateway gateway;

    public CadastrarPecaUseCase(CadastrarPecaGateway gateway) {
        this.gateway = gateway;
    }

    public Peca execute(CadastrarPecaRequest request) {
        Peca saved = gateway.salvarPeca(request.toDomain());
        gateway.salvarEstoque(PecaEstoque.create(saved.getId()));
        return saved;
    }
}
