package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.domain.entities.Servico;

public class BuscarServicoPorIdUseCase {

    private final BuscarServicoPorIdGateway gateway;

    public BuscarServicoPorIdUseCase(BuscarServicoPorIdGateway gateway) {
        this.gateway = gateway;
    }

    public Servico execute(Long id) {
        return gateway.getServicoPorId(id);
    }
}
