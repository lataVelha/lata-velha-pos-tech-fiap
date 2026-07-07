package br.com.lata.velha.ordem_servico.application.use_cases.peca;

import br.com.lata.velha.ordem_servico.domain.entities.Peca;

public class BuscarPecaPorIdUseCase {

    private final BuscarPecaPorIdGateway gateway;

    public BuscarPecaPorIdUseCase(BuscarPecaPorIdGateway gateway) {
        this.gateway = gateway;
    }

    public Peca execute(Long id) {
        return gateway.getPecaAtivaPorId(id);
    }
}
