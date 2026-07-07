package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;

public class BuscarProprietarioPorIdUseCase {

    private final BuscarProprietarioPorIdGateway gateway;

    public BuscarProprietarioPorIdUseCase(BuscarProprietarioPorIdGateway gateway) {
        this.gateway = gateway;
    }

    public Proprietario execute(Long id) {
        return gateway.getProprietarioPorId(id);
    }
}
