package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public class FinalizarDiagnosticoUseCase {

    private final FinalizarDiagnosticoGateway gateway;
    private final NotificarOrdemServicoService notificarService;

    public FinalizarDiagnosticoUseCase(FinalizarDiagnosticoGateway gateway,
                                       NotificarOrdemServicoService notificarService) {
        this.gateway = gateway;
        this.notificarService = notificarService;
    }

    public void execute(Input input) {
        var ordemServico = gateway.getOrdemServicoComServicos(input.idOs());
        gateway.getFuncionarioPorUserId(input.userId()); // validates user is a funcionario
        ordemServico.finalizarDiagnostico(ordemServico.getMecanicoResponsavelId());
        gateway.salvarOrdemServico(ordemServico);
        notificarService.execute(ordemServico);
    }

    public record Input(Long idOs, UserId userId) {}
}
