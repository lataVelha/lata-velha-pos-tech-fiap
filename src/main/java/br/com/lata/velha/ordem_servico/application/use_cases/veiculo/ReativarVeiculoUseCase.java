package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;

public class ReativarVeiculoUseCase {

    private final ReativarVeiculoGateway gateway;

    public ReativarVeiculoUseCase(ReativarVeiculoGateway gateway) {
        this.gateway = gateway;
    }

    public Veiculo execute(Long id) {
        Veiculo veiculo = gateway.getVeiculoInativoPorId(id);
        veiculo.activate();
        return gateway.salvarVeiculo(veiculo);
    }
}
