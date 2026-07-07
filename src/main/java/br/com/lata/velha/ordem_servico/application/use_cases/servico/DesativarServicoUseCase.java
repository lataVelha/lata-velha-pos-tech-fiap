package br.com.lata.velha.ordem_servico.application.use_cases.servico;

public class DesativarServicoUseCase {

    private final DesativarServicoGateway gateway;

    public DesativarServicoUseCase(DesativarServicoGateway gateway) {
        this.gateway = gateway;
    }

    public void execute(Long id) {
        var servico = gateway.getServicoPorId(id);
        servico.desativar();
        gateway.salvarServico(servico);
    }
}
