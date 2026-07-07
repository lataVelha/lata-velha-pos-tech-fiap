package br.com.lata.velha.ordem_servico.application.controllers.servico;

import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.CadastrarServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.ordem_servico.application.presenters.servico.*;
import br.com.lata.velha.ordem_servico.application.use_cases.servico.*;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public class ServicoCleanController {

    private final CadastrarServicoGateway cadastrarGateway;
    private final CadastrarServicoPresenter cadastrarPresenter;

    private final AtualizarServicoGateway atualizarGateway;
    private final AtualizarServicoPresenter atualizarPresenter;

    private final BuscarServicoPorIdGateway buscarPorIdGateway;
    private final BuscarServicoPorIdPresenter buscarPorIdPresenter;

    private final BuscarServicosGateway buscarTodosGateway;
    private final BuscarServicosPresenter buscarTodosPresenter;

    private final DesativarServicoGateway desativarGateway;

    public ServicoCleanController(CadastrarServicoGateway cadastrarGateway,
                                  CadastrarServicoPresenter cadastrarPresenter,
                                  AtualizarServicoGateway atualizarGateway,
                                  AtualizarServicoPresenter atualizarPresenter,
                                  BuscarServicoPorIdGateway buscarPorIdGateway,
                                  BuscarServicoPorIdPresenter buscarPorIdPresenter,
                                  BuscarServicosGateway buscarTodosGateway,
                                  BuscarServicosPresenter buscarTodosPresenter,
                                  DesativarServicoGateway desativarGateway) {
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

    public ServicoResponse cadastrar(CadastrarServicoRequest request) {
        return cadastrarPresenter.present(new CadastrarServicoUseCase(cadastrarGateway).execute(request));
    }

    public ServicoResponse atualizar(Long id, AtualizarServicoRequest request) {
        return atualizarPresenter.present(new AtualizarServicoUseCase(atualizarGateway).execute(id, request));
    }

    public ServicoResponse buscarPorId(Long id) {
        return buscarPorIdPresenter.present(new BuscarServicoPorIdUseCase(buscarPorIdGateway).execute(id));
    }

    public PaginatedResult<ServicoResponse> buscarTodos(int page, int size) {
        return buscarTodosPresenter.present(new BuscarServicosUseCase(buscarTodosGateway).execute(page, size));
    }

    public void desativar(Long id) {
        new DesativarServicoUseCase(desativarGateway).execute(id);
    }
}
