package br.com.lata.velha.ordem_servico.application.controllers.peca;

import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarPecaRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.CadastrarPecaRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaResponse;
import br.com.lata.velha.ordem_servico.application.presenters.peca.*;
import br.com.lata.velha.ordem_servico.application.use_cases.peca.*;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public class PecaCleanController {

    private final CadastrarPecaGateway cadastrarGateway;
    private final CadastrarPecaPresenter cadastrarPresenter;

    private final AtualizarPecaGateway atualizarGateway;
    private final AtualizarPecaPresenter atualizarPresenter;

    private final BuscarPecaPorIdGateway buscarPorIdGateway;
    private final BuscarPecaPorIdPresenter buscarPorIdPresenter;

    private final BuscarPecasGateway buscarTodosGateway;
    private final BuscarPecasPresenter buscarTodosPresenter;

    private final DesativarPecaGateway desativarGateway;

    public PecaCleanController(CadastrarPecaGateway cadastrarGateway,
                               CadastrarPecaPresenter cadastrarPresenter,
                               AtualizarPecaGateway atualizarGateway,
                               AtualizarPecaPresenter atualizarPresenter,
                               BuscarPecaPorIdGateway buscarPorIdGateway,
                               BuscarPecaPorIdPresenter buscarPorIdPresenter,
                               BuscarPecasGateway buscarTodosGateway,
                               BuscarPecasPresenter buscarTodosPresenter,
                               DesativarPecaGateway desativarGateway) {
        this.cadastrarGateway = cadastrarGateway;
        this.cadastrarPresenter = cadastrarPresenter;
        this.atualizarGateway = atualizarGateway;
        this.atualizarPresenter = atualizarPresenter;
        this.buscarPorIdGateway = buscarPorIdGateway;
        this.buscarPorIdPresenter = buscarPorIdPresenter;
        this.buscarTodosGateway = buscarTodosGateway;
        this.buscarTodosPresenter = buscarTodosPresenter;
        this.desativarGateway = desativarGateway;
    }

    public PecaResponse cadastrar(CadastrarPecaRequest request) {
        return cadastrarPresenter.present(new CadastrarPecaUseCase(cadastrarGateway).execute(request));
    }

    public PecaResponse atualizar(Long id, AtualizarPecaRequest request) {
        return atualizarPresenter.present(new AtualizarPecaUseCase(atualizarGateway).execute(id, request));
    }

    public PecaResponse buscarPorId(Long id) {
        return buscarPorIdPresenter.present(new BuscarPecaPorIdUseCase(buscarPorIdGateway).execute(id));
    }

    public PaginatedResult<PecaResponse> buscarTodos(int page, int size) {
        return buscarTodosPresenter.present(new BuscarPecasUseCase(buscarTodosGateway).execute(page, size));
    }

    public void desativar(Long id) {
        new DesativarPecaUseCase(desativarGateway).execute(id);
    }
}
