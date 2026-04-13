package br.com.lata.velha.ordemDeServico.application.assemblers;

import br.com.lata.velha.ordemDeServico.application.dtos.request.CadastrarServicoRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.ordemDeServico.domain.entities.Servico;
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
