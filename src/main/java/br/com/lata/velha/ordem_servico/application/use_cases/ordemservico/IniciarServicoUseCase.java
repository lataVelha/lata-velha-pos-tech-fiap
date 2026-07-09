package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public class IniciarServicoUseCase {

    private final IniciarServicoGateway gateway;
    private final NotificarOrdemServicoService notificarService;

    public IniciarServicoUseCase(IniciarServicoGateway gateway,
                                 NotificarOrdemServicoService notificarService) {
        this.gateway = gateway;
        this.notificarService = notificarService;
    }

    public void execute(Input input) {
        var ordemServico = gateway.getOrdemServicoComServicos(input.idOs());
        var mecanico = gateway.getFuncionarioPorUserId(input.userId());
        var primeiroServico = !ordemServico.isEmExecucao();
        ordemServico.iniciarExecucaoServico(input.servicoId(), mecanico.getId());
        var saved = gateway.salvarOrdemServico(ordemServico);
        if (primeiroServico)
            notificarService.execute(saved);
    }

    public record Input(Long idOs, Long servicoId, UserId userId) {}
}
