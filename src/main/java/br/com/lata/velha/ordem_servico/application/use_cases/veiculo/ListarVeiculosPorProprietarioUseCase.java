package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;

import java.util.List;

public class ListarVeiculosPorProprietarioUseCase {

    private final ListarVeiculosPorProprietarioGateway gateway;

    public ListarVeiculosPorProprietarioUseCase(ListarVeiculosPorProprietarioGateway gateway) {
        this.gateway = gateway;
    }

    public List<Veiculo> execute(Long proprietarioId) {
        return gateway.findByProprietarioId(proprietarioId);
    }
}
