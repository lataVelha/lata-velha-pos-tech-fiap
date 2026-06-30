package br.com.lata.velha.ordem_servico.application.controllers.pecaestoque;

import br.com.lata.velha.ordem_servico.application.dtos.request.AjustarPecaEstoqueRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.MovimentarPecaEstoqueRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaEstoqueResponse;
import br.com.lata.velha.ordem_servico.application.presenters.pecaestoque.*;
import br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque.*;

public class PecaEstoqueCleanController {

    private final BuscarPecaEstoqueGateway buscarGateway;
    private final BuscarPecaEstoquePresenter buscarPresenter;

    private final EntradaPecaEstoqueGateway entradaGateway;
    private final EntradaPecaEstoquePresenter entradaPresenter;

    private final SaidaPecaEstoqueGateway saidaGateway;
    private final SaidaPecaEstoquePresenter saidaPresenter;

    private final AjustarPecaEstoqueGateway ajustarGateway;
    private final AjustarPecaEstoquePresenter ajustarPresenter;

    public PecaEstoqueCleanController(BuscarPecaEstoqueGateway buscarGateway,
                                      BuscarPecaEstoquePresenter buscarPresenter,
                                      EntradaPecaEstoqueGateway entradaGateway,
                                      EntradaPecaEstoquePresenter entradaPresenter,
                                      SaidaPecaEstoqueGateway saidaGateway,
                                      SaidaPecaEstoquePresenter saidaPresenter,
                                      AjustarPecaEstoqueGateway ajustarGateway,
                                      AjustarPecaEstoquePresenter ajustarPresenter) {
        this.buscarGateway = buscarGateway;
        this.buscarPresenter = buscarPresenter;
        this.entradaGateway = entradaGateway;
        this.entradaPresenter = entradaPresenter;
        this.saidaGateway = saidaGateway;
        this.saidaPresenter = saidaPresenter;
        this.ajustarGateway = ajustarGateway;
        this.ajustarPresenter = ajustarPresenter;
    }

    public PecaEstoqueResponse buscar(Long pecaId) {
        return buscarPresenter.present(new BuscarPecaEstoqueUseCase(buscarGateway).execute(pecaId));
    }

    public PecaEstoqueResponse entrada(Long pecaId, MovimentarPecaEstoqueRequest request) {
        return entradaPresenter.present(new EntradaPecaEstoqueUseCase(entradaGateway).execute(pecaId, request));
    }

    public PecaEstoqueResponse saida(Long pecaId, MovimentarPecaEstoqueRequest request) {
        return saidaPresenter.present(new SaidaPecaEstoqueUseCase(saidaGateway).execute(pecaId, request));
    }

    public PecaEstoqueResponse ajustar(Long pecaId, AjustarPecaEstoqueRequest request) {
        return ajustarPresenter.present(new AjustarPecaEstoqueUseCase(ajustarGateway).execute(pecaId, request));
    }
}
