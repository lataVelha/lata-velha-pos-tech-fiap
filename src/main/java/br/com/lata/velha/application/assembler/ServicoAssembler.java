package br.com.lata.velha.application.assembler;

import br.com.lata.velha.application.dto.request.CadastrarServicoRequest;
import br.com.lata.velha.application.dto.response.ServicoResponse;
import br.com.lata.velha.domain.model.Servico;
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
