package br.com.lata.velha.ordem_servico.application.use_cases.peca;

import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public class BuscarPecasUseCase {

    private final BuscarPecasGateway gateway;

    public BuscarPecasUseCase(BuscarPecasGateway gateway) {
        this.gateway = gateway;
    }

    public PaginatedResult<Peca> execute(int page, int size) {
        return gateway.findAll(page, size);
    }
}
