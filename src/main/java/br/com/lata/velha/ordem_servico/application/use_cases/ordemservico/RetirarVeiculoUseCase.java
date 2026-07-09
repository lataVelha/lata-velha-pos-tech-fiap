package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public class RetirarVeiculoUseCase {

    private final RetirarVeiculoGateway gateway;
    private final NotificarOrdemServicoService notificarService;

    public RetirarVeiculoUseCase(RetirarVeiculoGateway gateway,
                                 NotificarOrdemServicoService notificarService) {
        this.gateway = gateway;
        this.notificarService = notificarService;
    }

    public void execute(Long idOs, UserId userId) {
        var ordemServico = gateway.getOrdemServicoComServicosEPecas(idOs);
        var funcionario = gateway.getFuncionarioPorUserId(userId);
        ordemServico.entregar(funcionario.getId());
        gateway.salvarOrdemServico(ordemServico);
        notificarService.execute(ordemServico);
    }
}
