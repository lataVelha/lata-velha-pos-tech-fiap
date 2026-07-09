package br.com.lata.velha.ordem_servico.application.controllers.ordemservico;

import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.AprovarOrdemServicoResponse;
import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.application.dtos.response.TempoMedioExecucaoResponse;
import br.com.lata.velha.ordem_servico.application.gateways.EmailProvider;
import br.com.lata.velha.ordem_servico.application.gateways.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.application.presenters.ordemservico.*;
import br.com.lata.velha.ordem_servico.application.services.ordemservico.*;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.*;
import br.com.lata.velha.ordem_servico.application.use_cases.proprietario.CriarProprietarioGateway;
import br.com.lata.velha.ordem_servico.application.use_cases.proprietario.NotificarCadastroProprietarioUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.veiculo.CriarVeiculoGateway;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.shared.domain.value_objects.UserId;

import java.time.LocalDate;

public class OrdemServicoCleanController {

    private final NotificarOrdemServicoService notificarService;
    private final NotificarAdminEncomendaPecaService notificarAdminService;
    private final EmailProvider emailProvider;
    private final EmailTemplateProvider templateProvider;

    private final CriarOrdemServicoGateway criarGateway;
    private final CriarOrdemServicoPresenter criarPresenter;
    private final CriarProprietarioGateway criarProprietarioGateway;
    private final CriarVeiculoGateway criarVeiculoGateway;
    private final AdicionarServicoGateway adicionarGateway;
    private final AprovarOrdemServicoGateway aprovarGateway;
    private final AprovarOrdemServicoPresenter aprovarPresenter;
    private final BuscarOrdemServicoGateway buscarGateway;
    private final BuscarOrdemServicoPresenter buscarPresenter;
    private final BuscarOrdensPorStatusOrdenadoGateway buscarOrdensPorStatusGateway;
    private final BuscarOrdensPorStatusPresenter buscarOrdensPorStatusPresenter;
    private final BuscarTempoMedioExecucaoGateway buscarTempoMedioGateway;
    private final BuscarTempoMedioExecucaoPresenter buscarTempoMedioPresenter;
    private final IniciarDiagnosticoGateway iniciarDiagnosticoGateway;
    private final FinalizarDiagnosticoGateway finalizarDiagnosticoGateway;
    private final ReprovarOrdemServicoGateway reprovarGateway;
    private final IniciarServicoGateway iniciarServicoGateway;
    private final FinalizarServicoGateway finalizarServicoGateway;
    private final RetirarVeiculoGateway retirarVeiculoGateway;
    private final ReceberAprovacaoOrcamentoClienteGateway receberAprovacaoGateway;
    private final ReceberAprovacaoOrcamentoClientePresenter receberAprovacaoPresenter;

    public OrdemServicoCleanController(
            NotificarOrdemServicoGateway notificarGateway,
            NotificarAdminEncomendaPecaGateway notificarAdminGateway,
            EmailProvider emailProvider,
            EmailTemplateProvider templateProvider,
            CriarOrdemServicoGateway criarGateway,
            CriarOrdemServicoPresenter criarPresenter,
            CriarProprietarioGateway criarProprietarioGateway,
            CriarVeiculoGateway criarVeiculoGateway,
            AdicionarServicoGateway adicionarGateway,
            AprovarOrdemServicoGateway aprovarGateway,
            AprovarOrdemServicoPresenter aprovarPresenter,
            BuscarOrdemServicoGateway buscarGateway,
            BuscarOrdemServicoPresenter buscarPresenter,
            BuscarOrdensPorStatusOrdenadoGateway buscarOrdensPorStatusGateway,
            BuscarOrdensPorStatusPresenter buscarOrdensPorStatusPresenter,
            BuscarTempoMedioExecucaoGateway buscarTempoMedioGateway,
            BuscarTempoMedioExecucaoPresenter buscarTempoMedioPresenter,
            IniciarDiagnosticoGateway iniciarDiagnosticoGateway,
            FinalizarDiagnosticoGateway finalizarDiagnosticoGateway,
            ReprovarOrdemServicoGateway reprovarGateway,
            IniciarServicoGateway iniciarServicoGateway,
            FinalizarServicoGateway finalizarServicoGateway,
            RetirarVeiculoGateway retirarVeiculoGateway,
            ReceberAprovacaoOrcamentoClienteGateway receberAprovacaoGateway,
            ReceberAprovacaoOrcamentoClientePresenter receberAprovacaoPresenter) {

        this.notificarService = new NotificarOrdemServicoService(notificarGateway, emailProvider, templateProvider);
        this.notificarAdminService = new NotificarAdminEncomendaPecaService(notificarAdminGateway, emailProvider, templateProvider);
        this.emailProvider = emailProvider;
        this.templateProvider = templateProvider;
        this.criarGateway = criarGateway;
        this.criarPresenter = criarPresenter;
        this.criarProprietarioGateway = criarProprietarioGateway;
        this.criarVeiculoGateway = criarVeiculoGateway;
        this.adicionarGateway = adicionarGateway;
        this.aprovarGateway = aprovarGateway;
        this.aprovarPresenter = aprovarPresenter;
        this.buscarGateway = buscarGateway;
        this.buscarPresenter = buscarPresenter;
        this.buscarOrdensPorStatusGateway = buscarOrdensPorStatusGateway;
        this.buscarOrdensPorStatusPresenter = buscarOrdensPorStatusPresenter;
        this.buscarTempoMedioGateway = buscarTempoMedioGateway;
        this.buscarTempoMedioPresenter = buscarTempoMedioPresenter;
        this.iniciarDiagnosticoGateway = iniciarDiagnosticoGateway;
        this.finalizarDiagnosticoGateway = finalizarDiagnosticoGateway;
        this.reprovarGateway = reprovarGateway;
        this.iniciarServicoGateway = iniciarServicoGateway;
        this.finalizarServicoGateway = finalizarServicoGateway;
        this.retirarVeiculoGateway = retirarVeiculoGateway;
        this.receberAprovacaoGateway = receberAprovacaoGateway;
        this.receberAprovacaoPresenter = receberAprovacaoPresenter;
    }

    public OrdemServicoResponse criar(CriarOrdemServicoUseCase.Input input) {
        var useCase = new CriarOrdemServicoUseCase(criarGateway, notificarService);
        return criarPresenter.present(useCase.execute(input));
    }

    public OrdemServicoResponse criarCompleta(CriarOrdemServicoCompletaUseCase.Input input) {
        var notificarCadastroProprietario = new NotificarCadastroProprietarioUseCase(emailProvider, templateProvider);
        var useCase = new CriarOrdemServicoCompletaUseCase(
                criarGateway, criarProprietarioGateway, criarVeiculoGateway, adicionarGateway,
                notificarService, notificarCadastroProprietario);
        return criarPresenter.present(useCase.execute(input));
    }

    public PaginatedResult<OrdemServicoResponse> buscar(Long id, StatusOrdemServico status,
                                                         Long proprietarioId, Long mecanicoId,
                                                         int page, int size) {
        var useCase = new BuscarOrdemServicoUseCase(buscarGateway);
        return buscarPresenter.present(useCase.execute(id, status, proprietarioId, mecanicoId, page, size));
    }

    public PaginatedResult<OrdemServicoResponse> buscarPorStatus(int page, int size) {
        var useCase = new BuscarOrdensPorStatusOrdenadoUseCase(buscarOrdensPorStatusGateway);
        return buscarOrdensPorStatusPresenter.present(useCase.execute(page, size));
    }

    public TempoMedioExecucaoResponse buscarTempoMedioExecucao(LocalDate dataInicio, LocalDate dataFim) {
        var useCase = new BuscarTempoMedioExecucaoServicosFinalizadosUseCase(buscarTempoMedioGateway);
        var result = useCase.execute(dataInicio, dataFim);
        return buscarTempoMedioPresenter.present(result.itens(), result.dataInicio(), result.dataFim());
    }

    public void iniciarDiagnostico(IniciarDiagnosticoUseCase.Input input) {
        new IniciarDiagnosticoUseCase(iniciarDiagnosticoGateway, notificarService).execute(input);
    }

    public void adicionarServico(AdicionarServicoUseCase.Input input) {
        new AdicionarServicoUseCase(adicionarGateway).execute(input);
    }

    public void finalizarDiagnostico(FinalizarDiagnosticoUseCase.Input input) {
        new FinalizarDiagnosticoUseCase(finalizarDiagnosticoGateway, notificarService).execute(input);
    }

    public AprovarOrdemServicoResponse aprovar(AprovarOrdemServicoUseCase.Input input) {
        var useCase = new AprovarOrdemServicoUseCase(aprovarGateway, notificarService, notificarAdminService);
        return aprovarPresenter.present(useCase.execute(input));
    }

    public void reprovar(ReprovarOrdemServicoUseCase.Input input) {
        new ReprovarOrdemServicoUseCase(reprovarGateway, notificarService).execute(input);
    }

    public void iniciarServico(IniciarServicoUseCase.Input input) {
        new IniciarServicoUseCase(iniciarServicoGateway, notificarService).execute(input);
    }

    public void finalizarServico(FinalizarServicoUseCase.Input input) {
        new FinalizarServicoUseCase(finalizarServicoGateway, notificarService).execute(input);
    }

    public void retirarVeiculo(Long idOs, UserId userId) {
        new RetirarVeiculoUseCase(retirarVeiculoGateway, notificarService).execute(idOs, userId);
    }

    public ReceberAprovacaoOrcamentoClientePresenter.ViewModel receberAprovacaoOrcamentoCliente(
            ReceberAprovacaoOrcamentoClienteUseCase.Input input) {
        var useCase = new ReceberAprovacaoOrcamentoClienteUseCase(receberAprovacaoGateway, notificarService);
        return receberAprovacaoPresenter.present(useCase.execute(input));
    }
}
