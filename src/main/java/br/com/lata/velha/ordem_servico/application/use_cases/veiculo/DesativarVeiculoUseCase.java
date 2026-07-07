package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

public class DesativarVeiculoUseCase {

    private final DesativarVeiculoGateway gateway;

    public DesativarVeiculoUseCase(DesativarVeiculoGateway gateway) {
        this.gateway = gateway;
    }

    public void execute(Long id) {
        var veiculo = gateway.getVeiculoPorId(id);
        veiculo.deactivate();
        gateway.salvarVeiculo(veiculo);
    }
}
