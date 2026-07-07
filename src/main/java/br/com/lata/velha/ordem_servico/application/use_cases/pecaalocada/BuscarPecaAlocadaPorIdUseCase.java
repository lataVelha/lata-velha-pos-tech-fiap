package br.com.lata.velha.ordem_servico.application.use_cases.pecaalocada;

import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;

public class BuscarPecaAlocadaPorIdUseCase {

    private final BuscarPecaAlocadaPorIdGateway gateway;

    public BuscarPecaAlocadaPorIdUseCase(BuscarPecaAlocadaPorIdGateway gateway) {
        this.gateway = gateway;
    }

    public PecaAlocada execute(Long id) {
        return gateway.getPecaAlocadaPorId(id);
    }
}
