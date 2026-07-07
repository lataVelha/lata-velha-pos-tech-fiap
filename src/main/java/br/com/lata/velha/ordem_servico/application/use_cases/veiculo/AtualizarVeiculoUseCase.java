package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.application.dtos.request.VeiculoRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;

public class AtualizarVeiculoUseCase {

    private final AtualizarVeiculoGateway gateway;

    public AtualizarVeiculoUseCase(AtualizarVeiculoGateway gateway) {
        this.gateway = gateway;
    }

    public Veiculo execute(Long id, VeiculoRequest request) {
        Veiculo existing = gateway.getVeiculoPorId(id);
        gateway.getProprietarioAtivoPorId(request.proprietarioId());
        request.updateDomain(existing);
        return gateway.salvarVeiculo(existing);
    }
}
