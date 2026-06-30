package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;

public class BuscarVeiculoPorIdUseCase {

    private final BuscarVeiculoPorIdGateway gateway;

    public BuscarVeiculoPorIdUseCase(BuscarVeiculoPorIdGateway gateway) {
        this.gateway = gateway;
    }

    public Veiculo execute(Long id) {
        return gateway.getVeiculoPorId(id);
    }
}
