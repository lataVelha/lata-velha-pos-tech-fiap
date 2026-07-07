package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public class ListarVeiculosUseCase {

    private final ListarVeiculosGateway gateway;

    public ListarVeiculosUseCase(ListarVeiculosGateway gateway) {
        this.gateway = gateway;
    }

    public PaginatedResult<Veiculo> execute(int page, int size) {
        return gateway.findAll(page, size);
    }
}
