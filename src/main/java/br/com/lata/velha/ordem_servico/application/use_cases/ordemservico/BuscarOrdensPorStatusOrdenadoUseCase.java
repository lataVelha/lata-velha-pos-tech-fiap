package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public class BuscarOrdensPorStatusOrdenadoUseCase {

    private final BuscarOrdensPorStatusOrdenadoGateway gateway;

    public BuscarOrdensPorStatusOrdenadoUseCase(BuscarOrdensPorStatusOrdenadoGateway gateway) {
        this.gateway = gateway;
    }

    public PaginatedResult<OrdemServicoProjection> execute(int page, int size) {
        return gateway.findOrderedByStatusPriority(page, size);
    }
}
