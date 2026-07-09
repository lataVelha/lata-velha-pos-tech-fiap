package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public class IniciarDiagnosticoUseCase {

    private final IniciarDiagnosticoGateway gateway;
    private final NotificarOrdemServicoService notificarService;

    public IniciarDiagnosticoUseCase(IniciarDiagnosticoGateway gateway,
                                     NotificarOrdemServicoService notificarService) {
        this.gateway = gateway;
        this.notificarService = notificarService;
    }

    public void execute(Input input) {
        var ordemServico = gateway.getOrdemServicoPorId(input.idOs());
        var mecanico = gateway.getFuncionarioPorUserId(input.userId());
        ordemServico.iniciarDiagnostico(mecanico.getId());
        var saved = gateway.salvarOrdemServico(ordemServico);
        notificarService.execute(saved);
    }

    public record Input(Long idOs, UserId userId) {}
}
