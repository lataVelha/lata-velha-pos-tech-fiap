package br.com.lata.velha.ordem_servico.application.use_cases.peca;

import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarPecaRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;

public class AtualizarPecaUseCase {

    private final AtualizarPecaGateway gateway;

    public AtualizarPecaUseCase(AtualizarPecaGateway gateway) {
        this.gateway = gateway;
    }

    public Peca execute(Long id, AtualizarPecaRequest request) {
        Peca peca = gateway.getPecaAtivaPorId(id);
        peca.atualizar(request.nome(), request.descricao(), request.valor());
        return gateway.salvarPeca(peca);
    }
}
