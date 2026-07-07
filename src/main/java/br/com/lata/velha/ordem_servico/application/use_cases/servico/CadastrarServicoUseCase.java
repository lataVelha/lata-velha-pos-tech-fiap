package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.application.dtos.request.CadastrarServicoRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;

public class CadastrarServicoUseCase {

    private final CadastrarServicoGateway gateway;

    public CadastrarServicoUseCase(CadastrarServicoGateway gateway) {
        this.gateway = gateway;
    }

    public Servico execute(CadastrarServicoRequest request) {
        return gateway.salvarServico(request.toDomain());
    }
}
