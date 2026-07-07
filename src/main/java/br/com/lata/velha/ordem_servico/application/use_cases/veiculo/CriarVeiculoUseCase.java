package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.application.dtos.request.VeiculoRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;

public class CriarVeiculoUseCase {

    private final CriarVeiculoGateway gateway;

    public CriarVeiculoUseCase(CriarVeiculoGateway gateway) {
        this.gateway = gateway;
    }

    public Veiculo execute(VeiculoRequest request) {
        gateway.getProprietarioAtivoPorId(request.proprietarioId());
        return gateway.salvarVeiculo(request.toDomain());
    }
}
