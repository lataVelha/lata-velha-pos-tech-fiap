package br.com.lata.velha.ordem_servico.application.assemblers;

import br.com.lata.velha.ordem_servico.application.dtos.request.CadastrarServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import org.springframework.stereotype.Component;

@Component
public class ServicoAssembler {

    public Servico toDomain(CadastrarServicoRequest request) {
        return new Servico(
                null,
                request.nome(),
                request.descricao(),
                true
        );
    }

    public ServicoResponse toResponse(Servico model) {
        return new ServicoResponse(
                model.getId(),
                model.getNome(),
                model.getDescricao()
        );
    }
}
