package br.com.lata.velha.ordem_servico.application.controllers.veiculo;

import br.com.lata.velha.ordem_servico.application.dtos.request.VeiculoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordem_servico.application.presenters.veiculo.*;
import br.com.lata.velha.ordem_servico.application.use_cases.veiculo.*;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

import java.util.List;

public class VeiculoCleanController {

    private final CriarVeiculoGateway criarGateway;
    private final CriarVeiculoPresenter criarPresenter;

    private final AtualizarVeiculoGateway atualizarGateway;
    private final AtualizarVeiculoPresenter atualizarPresenter;

    private final BuscarVeiculoPorIdGateway buscarPorIdGateway;
    private final BuscarVeiculoPorIdPresenter buscarPorIdPresenter;

    private final ListarVeiculosGateway listarGateway;
    private final ListarVeiculosPresenter listarPresenter;

    private final ListarVeiculosPorProprietarioGateway listarPorProprietarioGateway;
    private final ListarVeiculosPorProprietarioPresenter listarPorProprietarioPresenter;

    private final DesativarVeiculoGateway desativarGateway;

    private final ReativarVeiculoGateway reativarGateway;
    private final ReativarVeiculoPresenter reativarPresenter;

    public VeiculoCleanController(CriarVeiculoGateway criarGateway,
                                  CriarVeiculoPresenter criarPresenter,
                                  AtualizarVeiculoGateway atualizarGateway,
                                  AtualizarVeiculoPresenter atualizarPresenter,
                                  BuscarVeiculoPorIdGateway buscarPorIdGateway,
                                  BuscarVeiculoPorIdPresenter buscarPorIdPresenter,
                                  ListarVeiculosGateway listarGateway,
                                  ListarVeiculosPresenter listarPresenter,
                                  ListarVeiculosPorProprietarioGateway listarPorProprietarioGateway,
                                  ListarVeiculosPorProprietarioPresenter listarPorProprietarioPresenter,
                                  DesativarVeiculoGateway desativarGateway,
                                  ReativarVeiculoGateway reativarGateway,
                                  ReativarVeiculoPresenter reativarPresenter) {
        this.criarGateway = criarGateway;
        this.criarPresenter = criarPresenter;
        this.atualizarGateway = atualizarGateway;
        this.atualizarPresenter = atualizarPresenter;
        this.buscarPorIdGateway = buscarPorIdGateway;
        this.buscarPorIdPresenter = buscarPorIdPresenter;
        this.listarGateway = listarGateway;
        this.listarPresenter = listarPresenter;
        this.listarPorProprietarioGateway = listarPorProprietarioGateway;
        this.listarPorProprietarioPresenter = listarPorProprietarioPresenter;
        this.desativarGateway = desativarGateway;
        this.reativarGateway = reativarGateway;
        this.reativarPresenter = reativarPresenter;
    }

    public VeiculoResponse criar(VeiculoRequest request) {
        return criarPresenter.present(new CriarVeiculoUseCase(criarGateway).execute(request));
    }

    public VeiculoResponse atualizar(Long id, VeiculoRequest request) {
        return atualizarPresenter.present(new AtualizarVeiculoUseCase(atualizarGateway).execute(id, request));
    }

    public VeiculoResponse buscarPorId(Long id) {
        return buscarPorIdPresenter.present(new BuscarVeiculoPorIdUseCase(buscarPorIdGateway).execute(id));
    }

    public PaginatedResult<VeiculoResponse> listar(int page, int size) {
        return listarPresenter.present(new ListarVeiculosUseCase(listarGateway).execute(page, size));
    }

    public List<VeiculoResponse> listarPorProprietario(Long proprietarioId) {
        return listarPorProprietarioPresenter.present(new ListarVeiculosPorProprietarioUseCase(listarPorProprietarioGateway).execute(proprietarioId));
    }

    public void desativar(Long id) {
        new DesativarVeiculoUseCase(desativarGateway).execute(id);
    }

    public VeiculoResponse reativar(Long id) {
        return reativarPresenter.present(new ReativarVeiculoUseCase(reativarGateway).execute(id));
    }
}
