package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.shared.domain.value_objects.UserId;

public class RetirarVeiculoUseCase {

    private final RetirarVeiculoGateway gateway;
    private final NotificarOrdemServicoUseCase notificarUseCase;

    public RetirarVeiculoUseCase(RetirarVeiculoGateway gateway,
                                 NotificarOrdemServicoUseCase notificarUseCase) {
        this.gateway = gateway;
        this.notificarUseCase = notificarUseCase;
    }

    public void execute(Long idOs, UserId userId) {
        var ordemServico = gateway.getOrdemServicoComServicosEPecas(idOs);
        var funcionario = gateway.getFuncionarioPorUserId(userId);
        ordemServico.entregar(funcionario.getId());
        gateway.salvarOrdemServico(ordemServico);
        notificarUseCase.execute(ordemServico);
    }
}
