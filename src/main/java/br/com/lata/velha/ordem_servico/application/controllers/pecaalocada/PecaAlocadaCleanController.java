package br.com.lata.velha.ordem_servico.application.controllers.pecaalocada;

import br.com.lata.velha.ordem_servico.application.dtos.response.PecaAlocadaResponse;
import br.com.lata.velha.ordem_servico.application.presenters.pecaalocada.BuscarPecaAlocadaPorIdPresenter;
import br.com.lata.velha.ordem_servico.application.presenters.pecaalocada.BuscarPecasAlocadasPresenter;
import br.com.lata.velha.ordem_servico.application.use_cases.pecaalocada.BuscarPecaAlocadaPorIdGateway;
import br.com.lata.velha.ordem_servico.application.use_cases.pecaalocada.BuscarPecaAlocadaPorIdUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.pecaalocada.BuscarPecasAlocadasGateway;
import br.com.lata.velha.ordem_servico.application.use_cases.pecaalocada.BuscarPecasAlocadasUseCase;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public class PecaAlocadaCleanController {

    private final BuscarPecaAlocadaPorIdGateway buscarPorIdGateway;
    private final BuscarPecaAlocadaPorIdPresenter buscarPorIdPresenter;

    private final BuscarPecasAlocadasGateway buscarTodosGateway;
    private final BuscarPecasAlocadasPresenter buscarTodosPresenter;

    public PecaAlocadaCleanController(BuscarPecaAlocadaPorIdGateway buscarPorIdGateway,
                                      BuscarPecaAlocadaPorIdPresenter buscarPorIdPresenter,
                                      BuscarPecasAlocadasGateway buscarTodosGateway,
                                      BuscarPecasAlocadasPresenter buscarTodosPresenter) {
        this.buscarPorIdGateway = buscarPorIdGateway;
        this.buscarPorIdPresenter = buscarPorIdPresenter;
        this.buscarTodosGateway = buscarTodosGateway;
        this.buscarTodosPresenter = buscarTodosPresenter;
    }

    public PecaAlocadaResponse buscarPorId(Long id) {
        return buscarPorIdPresenter.present(new BuscarPecaAlocadaPorIdUseCase(buscarPorIdGateway).execute(id));
    }

    public PaginatedResult<PecaAlocadaResponse> buscarTodos(Long execucaoServicoId, int page, int size) {
        return buscarTodosPresenter.present(new BuscarPecasAlocadasUseCase(buscarTodosGateway).execute(execucaoServicoId, page, size));
    }
}
