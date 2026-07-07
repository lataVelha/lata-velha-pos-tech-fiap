package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public class BuscarOrdemServicoUseCase {

    private final BuscarOrdemServicoGateway gateway;

    public BuscarOrdemServicoUseCase(BuscarOrdemServicoGateway gateway) {
        this.gateway = gateway;
    }

    public PaginatedResult<OrdemServicoProjection> execute(Long id,
                                                           StatusOrdemServico status,
                                                           Long proprietarioId,
                                                           Long mecanicoId,
                                                           int page,
                                                           int size) {
        return gateway.findByFiltros(
                id,
                status != null ? status.name() : null,
                proprietarioId,
                mecanicoId,
                page,
                size
        );
    }
}
