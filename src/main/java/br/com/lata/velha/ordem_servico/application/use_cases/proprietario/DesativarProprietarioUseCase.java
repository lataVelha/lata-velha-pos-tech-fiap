package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

public class DesativarProprietarioUseCase {

    private final DesativarProprietarioGateway gateway;

    public DesativarProprietarioUseCase(DesativarProprietarioGateway gateway) {
        this.gateway = gateway;
    }

    public void execute(Long id) {
        var proprietario = gateway.getProprietarioPorId(id);
        proprietario.deactivate();
        gateway.salvarProprietario(proprietario);
    }
}
