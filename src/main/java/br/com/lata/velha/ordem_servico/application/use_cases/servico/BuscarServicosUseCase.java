package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public class BuscarServicosUseCase {

    private final BuscarServicosGateway gateway;

    public BuscarServicosUseCase(BuscarServicosGateway gateway) {
        this.gateway = gateway;
    }

    public PaginatedResult<Servico> execute(int page, int size) {
        return gateway.findAll(page, size);
    }
}
