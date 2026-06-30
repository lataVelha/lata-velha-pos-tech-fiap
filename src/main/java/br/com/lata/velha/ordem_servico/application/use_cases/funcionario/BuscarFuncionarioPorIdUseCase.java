package br.com.lata.velha.ordem_servico.application.use_cases.funcionario;

import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;

public class BuscarFuncionarioPorIdUseCase {

    private final BuscarFuncionarioPorIdGateway gateway;

    public BuscarFuncionarioPorIdUseCase(BuscarFuncionarioPorIdGateway gateway) {
        this.gateway = gateway;
    }

    public Funcionario execute(Long id) {
        return gateway.getFuncionarioById(id);
    }
}
