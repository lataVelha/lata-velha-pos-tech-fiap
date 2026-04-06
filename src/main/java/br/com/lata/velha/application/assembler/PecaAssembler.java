package br.com.lata.velha.application.assembler;

import br.com.lata.velha.application.dto.request.CadastrarPecaRequest;
import br.com.lata.velha.application.dto.response.PecaResponse;
import br.com.lata.velha.domain.model.Peca;
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
