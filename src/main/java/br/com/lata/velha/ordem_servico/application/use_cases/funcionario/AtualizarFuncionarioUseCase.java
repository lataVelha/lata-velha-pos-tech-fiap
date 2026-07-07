package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.authentication.domain.exceptions.InactiveUserException;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;

public class AtualizarFuncionarioUseCase {

    private final AtualizarFuncionarioGateway gateway;

    public AtualizarFuncionarioUseCase(AtualizarFuncionarioGateway gateway) {
        this.gateway = gateway;
    }

    public Funcionario execute(Input input) {
        var funcionario = gateway.getFuncionarioById(input.id());
        if (!gateway.isUsuarioAtivo(funcionario.getUserId()))
            throw InactiveUserException.fromEntityName("Funcionário");

        var cargo = gateway.getCargoPorId(input.cargoId());
        funcionario.update(input.nome(), cargo);
        return gateway.salvarFuncionario(funcionario);
    }

    public record Input(Long id, String nome, Long cargoId) {}
}
