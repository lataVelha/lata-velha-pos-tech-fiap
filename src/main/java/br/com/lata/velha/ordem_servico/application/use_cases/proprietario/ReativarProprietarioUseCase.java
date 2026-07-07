package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;

public class ReativarProprietarioUseCase {

    private final ReativarProprietarioGateway gateway;

    public ReativarProprietarioUseCase(ReativarProprietarioGateway gateway) {
        this.gateway = gateway;
    }

    public Proprietario execute(Long id) {
        Proprietario proprietario = gateway.getProprietarioInativoPorId(id);
        proprietario.activate();
        return gateway.salvarProprietario(proprietario);
    }
}
