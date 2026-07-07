package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarServicoRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;

public class AtualizarServicoUseCase {

    private final AtualizarServicoGateway gateway;

    public AtualizarServicoUseCase(AtualizarServicoGateway gateway) {
        this.gateway = gateway;
    }

    public Servico execute(Long id, AtualizarServicoRequest request) {
        Servico servico = gateway.getServicoPorId(id);
        servico.atualizar(request.nome(), request.descricao());
        return gateway.salvarServico(servico);
    }
}
