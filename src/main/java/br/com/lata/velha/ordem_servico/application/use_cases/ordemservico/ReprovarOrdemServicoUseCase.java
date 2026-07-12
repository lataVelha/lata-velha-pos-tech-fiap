package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public class ReprovarOrdemServicoUseCase {

    private final ReprovarOrdemServicoGateway gateway;
    private final NotificarOrdemServicoService notificarService;

    public ReprovarOrdemServicoUseCase(ReprovarOrdemServicoGateway gateway,
                                       NotificarOrdemServicoService notificarService) {
        this.gateway = gateway;
        this.notificarService = notificarService;
    }

    public void execute(Input input) {
        var ordemServico = gateway.getOrdemServicoPorId(input.osId());
        var funcionario = gateway.getFuncionarioPorUserId(input.userId());

        ordemServico.getExecucaoServicos().forEach(execucaoServico ->
                execucaoServico.recusar(funcionario.getId())
        );
        ordemServico.reprovar(funcionario.getId());
        gateway.salvarOrdemServico(ordemServico);
        notificarService.execute(ordemServico);
    }

    public record Input(Long osId, UserId userId) {}
}
