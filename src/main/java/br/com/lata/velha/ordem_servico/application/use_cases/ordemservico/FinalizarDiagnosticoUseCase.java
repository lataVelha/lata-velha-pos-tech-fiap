package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.shared.domain.value_objects.UserId;

public class FinalizarDiagnosticoUseCase {

    private final FinalizarDiagnosticoGateway gateway;
    private final NotificarOrdemServicoUseCase notificarUseCase;

    public FinalizarDiagnosticoUseCase(FinalizarDiagnosticoGateway gateway,
                                       NotificarOrdemServicoUseCase notificarUseCase) {
        this.gateway = gateway;
        this.notificarUseCase = notificarUseCase;
    }

    public void execute(Input input) {
        var ordemServico = gateway.getOrdemServicoComServicos(input.idOs());
        gateway.getFuncionarioPorUserId(input.userId()); // validates user is a funcionario
        ordemServico.finalizarDiagnostico(ordemServico.getMecanicoResponsavelId());
        gateway.salvarOrdemServico(ordemServico);
        notificarUseCase.execute(ordemServico);
    }

    public record Input(Long idOs, UserId userId) {}
}
