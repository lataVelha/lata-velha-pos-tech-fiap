package br.com.lata.velha.ordem_servico.application.controllers.funcionario;

import br.com.lata.velha.ordem_servico.application.dtos.response.FuncionarioResponse;
import br.com.lata.velha.ordem_servico.application.gateways.authentication.AuthenticationService;
import br.com.lata.velha.ordem_servico.application.presenters.funcionario.AtualizarFuncionarioPresenter;
import br.com.lata.velha.ordem_servico.application.presenters.funcionario.BuscarFuncionarioPorIdPresenter;
import br.com.lata.velha.ordem_servico.application.presenters.funcionario.CadastrarFuncionarioPresenter;
import br.com.lata.velha.ordem_servico.application.use_cases.funcionario.*;

public class FuncionarioCleanController {

    private final CadastrarFuncionarioGateway cadastrarGateway;
    private final AuthenticationService authService;
    private final CadastrarFuncionarioPresenter cadastrarPresenter;

    private final AtualizarFuncionarioGateway atualizarGateway;
    private final AtualizarFuncionarioPresenter atualizarPresenter;

    private final BuscarFuncionarioPorIdGateway buscarGateway;
    private final BuscarFuncionarioPorIdPresenter buscarPresenter;

    private final DesativarFuncionarioGateway desativarGateway;

    public FuncionarioCleanController(CadastrarFuncionarioGateway cadastrarGateway,
                                      AuthenticationService authService,
                                      CadastrarFuncionarioPresenter cadastrarPresenter,
                                      AtualizarFuncionarioGateway atualizarGateway,
                                      AtualizarFuncionarioPresenter atualizarPresenter,
                                      BuscarFuncionarioPorIdGateway buscarGateway,
                                      BuscarFuncionarioPorIdPresenter buscarPresenter,
                                      DesativarFuncionarioGateway desativarGateway) {
        this.cadastrarGateway = cadastrarGateway;
        this.authService = authService;
        this.cadastrarPresenter = cadastrarPresenter;
        this.atualizarGateway = atualizarGateway;
        this.atualizarPresenter = atualizarPresenter;
        this.buscarGateway = buscarGateway;
        this.buscarPresenter = buscarPresenter;
        this.desativarGateway = desativarGateway;
    }

    public FuncionarioResponse cadastrar(CadastrarFuncionarioUseCase.Input input) {
        return cadastrarPresenter.present(
                new CadastrarFuncionarioUseCase(cadastrarGateway, authService).execute(input)
        );
    }

    public FuncionarioResponse buscarPorId(Long id) {
        return buscarPresenter.present(
                new BuscarFuncionarioPorIdUseCase(buscarGateway).execute(id)
        );
    }

    public FuncionarioResponse atualizar(AtualizarFuncionarioUseCase.Input input) {
        return atualizarPresenter.present(
                new AtualizarFuncionarioUseCase(atualizarGateway).execute(input)
        );
    }

    public void desativar(Long id) {
        new DesativarFuncionarioUseCase(desativarGateway).execute(id);
    }
}
