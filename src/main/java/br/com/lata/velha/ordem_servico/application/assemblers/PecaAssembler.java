package br.com.lata.velha.ordem_servico.application.assemblers;

import br.com.lata.velha.ordem_servico.application.dtos.request.CadastrarPecaRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import org.springframework.stereotype.Component;

@Component
public class PecaAssembler {

    public Peca toDomain(CadastrarPecaRequest request) {
        return new Peca(
                null,
                request.nome(),
                request.descricao(),
                request.valor(),
                true
        );
    }

    public PecaResponse toResponse(Peca model) {
        return new PecaResponse(
                model.getId(),
                model.getNome(),
                model.getDescricao(),
                model.getValor(),
                model.isAtivo()
        );
    }
}
