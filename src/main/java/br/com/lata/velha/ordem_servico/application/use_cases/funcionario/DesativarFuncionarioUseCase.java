package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

public class DesativarFuncionarioUseCase {

    private final DesativarFuncionarioGateway gateway;

    public DesativarFuncionarioUseCase(DesativarFuncionarioGateway gateway) {
        this.gateway = gateway;
    }

    public void execute(Long id) {
        var funcionario = gateway.getFuncionarioById(id);
        gateway.desativarUsuario(funcionario.getUserId());
    }
}
