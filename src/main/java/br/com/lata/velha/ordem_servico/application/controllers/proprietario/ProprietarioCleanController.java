package br.com.lata.velha.ordem_servico.application.controllers.proprietario;

import br.com.lata.velha.ordem_servico.application.dtos.request.ProprietarioRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.ordem_servico.application.gateways.EmailProvider;
import br.com.lata.velha.ordem_servico.application.gateways.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.application.presenters.proprietario.*;
import br.com.lata.velha.ordem_servico.application.use_cases.proprietario.*;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public class ProprietarioCleanController {

    private final CriarProprietarioGateway criarGateway;
    private final CriarProprietarioPresenter criarPresenter;

    private final AtualizarProprietarioGateway atualizarGateway;
    private final AtualizarProprietarioPresenter atualizarPresenter;

    private final BuscarProprietarioPorIdGateway buscarPorIdGateway;
    private final BuscarProprietarioPorIdPresenter buscarPorIdPresenter;

    private final BuscarProprietarioPorDocumentoGateway buscarPorDocumentoGateway;
    private final BuscarProprietarioPorDocumentoPresenter buscarPorDocumentoPresenter;

    private final ListarProprietariosGateway listarGateway;
    private final ListarProprietariosPresenter listarPresenter;

    private final DesativarProprietarioGateway desativarGateway;

    private final ReativarProprietarioGateway reativarGateway;
    private final ReativarProprietarioPresenter reativarPresenter;

    private final EmailProvider emailProvider;
    private final EmailTemplateProvider templateProvider;

    public ProprietarioCleanController(CriarProprietarioGateway criarGateway,
                                       CriarProprietarioPresenter criarPresenter,
                                       AtualizarProprietarioGateway atualizarGateway,
                                       AtualizarProprietarioPresenter atualizarPresenter,
                                       BuscarProprietarioPorIdGateway buscarPorIdGateway,
                                       BuscarProprietarioPorIdPresenter buscarPorIdPresenter,
                                       BuscarProprietarioPorDocumentoGateway buscarPorDocumentoGateway,
                                       BuscarProprietarioPorDocumentoPresenter buscarPorDocumentoPresenter,
                                       ListarProprietariosGateway listarGateway,
                                       ListarProprietariosPresenter listarPresenter,
                                       DesativarProprietarioGateway desativarGateway,
                                       ReativarProprietarioGateway reativarGateway,
                                       ReativarProprietarioPresenter reativarPresenter,
                                       EmailProvider emailProvider,
                                       EmailTemplateProvider templateProvider) {
        this.criarGateway = criarGateway;
        this.criarPresenter = criarPresenter;
        this.atualizarGateway = atualizarGateway;
        this.atualizarPresenter = atualizarPresenter;
        this.buscarPorIdGateway = buscarPorIdGateway;
        this.buscarPorIdPresenter = buscarPorIdPresenter;
        this.buscarPorDocumentoGateway = buscarPorDocumentoGateway;
        this.buscarPorDocumentoPresenter = buscarPorDocumentoPresenter;
        this.listarGateway = listarGateway;
        this.listarPresenter = listarPresenter;
        this.desativarGateway = desativarGateway;
        this.reativarGateway = reativarGateway;
        this.reativarPresenter = reativarPresenter;
        this.emailProvider = emailProvider;
        this.templateProvider = templateProvider;
    }

    public ProprietarioResponse criar(ProprietarioRequest request) {
        var notificar = new NotificarCadastroProprietarioUseCase(emailProvider, templateProvider);
        return criarPresenter.present(new CriarProprietarioUseCase(criarGateway, notificar).execute(request));
    }

    public ProprietarioResponse atualizar(Long id, ProprietarioRequest request) {
        return atualizarPresenter.present(new AtualizarProprietarioUseCase(atualizarGateway).execute(id, request));
    }

    public ProprietarioResponse buscarPorId(Long id) {
        return buscarPorIdPresenter.present(new BuscarProprietarioPorIdUseCase(buscarPorIdGateway).execute(id));
    }

    public ProprietarioResponse buscarPorDocumento(String documento) {
        return buscarPorDocumentoPresenter.present(new BuscarProprietarioPorDocumentoUseCase(buscarPorDocumentoGateway).execute(documento));
    }

    public PaginatedResult<ProprietarioResponse> listar(int page, int size) {
        return listarPresenter.present(new ListarProprietariosUseCase(listarGateway).execute(page, size));
    }

    public void desativar(Long id) {
        new DesativarProprietarioUseCase(desativarGateway).execute(id);
    }

    public ProprietarioResponse reativar(Long id) {
        return reativarPresenter.present(new ReativarProprietarioUseCase(reativarGateway).execute(id));
    }
}
