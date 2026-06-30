package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.shared.domain.value_objects.UserId;

public class IniciarServicoUseCase {

    private final IniciarServicoGateway gateway;
    private final NotificarOrdemServicoUseCase notificarUseCase;

    public IniciarServicoUseCase(IniciarServicoGateway gateway,
                                 NotificarOrdemServicoUseCase notificarUseCase) {
        this.gateway = gateway;
        this.notificarUseCase = notificarUseCase;
    }

    public void execute(Input input) {
        var ordemServico = gateway.getOrdemServicoComServicos(input.idOs());
        var mecanico = gateway.getFuncionarioPorUserId(input.userId());
        var primeiroServico = !ordemServico.isEmExecucao();
        ordemServico.iniciarExecucaoServico(input.servicoId(), mecanico.getId());
        var saved = gateway.salvarOrdemServico(ordemServico);
        if (primeiroServico)
            notificarUseCase.execute(saved);
    }

    public record Input(Long idOs, Long servicoId, UserId userId) {}
}
