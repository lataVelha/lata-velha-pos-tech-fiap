package br.com.lata.velha.ordem_servico.application.assemblers;

import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.CadastrarServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import org.springframework.stereotype.Component;

@Component
public class ServicoAssembler {

    public Servico toDomain(CadastrarServicoRequest request) {
        return request.toDomain();
    }

    public void updateDomain(Servico existente, AtualizarServicoRequest request) {
        existente.atualizar(request.nome(), request.descricao());
    }

    public ServicoResponse toResponse(Servico servico) {
        return ServicoResponse.from(servico);
    }
}
