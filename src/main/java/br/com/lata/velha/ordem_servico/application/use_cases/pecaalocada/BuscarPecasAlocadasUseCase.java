package br.com.lata.velha.ordem_servico.application.use_cases.pecaalocada;

import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public class BuscarPecasAlocadasUseCase {

    private final BuscarPecasAlocadasGateway gateway;

    public BuscarPecasAlocadasUseCase(BuscarPecasAlocadasGateway gateway) {
        this.gateway = gateway;
    }

    public PaginatedResult<PecaAlocada> execute(Long servicoOsId, int page, int size) {
        return gateway.findByExecucaoServicoId(servicoOsId, page, size);
    }
}
