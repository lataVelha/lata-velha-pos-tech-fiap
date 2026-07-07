package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.shared.domain.value_objects.UserId;

public class ReprovarOrdemServicoUseCase {

    private final ReprovarOrdemServicoGateway gateway;
    private final NotificarOrdemServicoUseCase notificarUseCase;

    public ReprovarOrdemServicoUseCase(ReprovarOrdemServicoGateway gateway,
                                       NotificarOrdemServicoUseCase notificarUseCase) {
        this.gateway = gateway;
        this.notificarUseCase = notificarUseCase;
    }

    public void execute(Input input) {
        var ordemServico = gateway.getOrdemServicoPorId(input.osId());
        var funcionario = gateway.getFuncionarioPorUserId(input.userId());

        ordemServico.getExecucaoServicos().forEach(execucaoServico ->
                execucaoServico.recusar(funcionario.getId())
        );
        ordemServico.reprovar(funcionario.getId());
        gateway.salvarOrdemServico(ordemServico);
        notificarUseCase.execute(ordemServico);
    }

    public record Input(Long osId, UserId userId) {}
}
