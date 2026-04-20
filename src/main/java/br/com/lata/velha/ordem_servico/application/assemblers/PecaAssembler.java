package br.com.lata.velha.ordem_servico.application.assemblers;

import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarPecaRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.CadastrarPecaRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import org.springframework.stereotype.Component;

@Component
public class PecaAssembler {

    public Peca toDomain(CadastrarPecaRequest request) {
        return request.toDomain();
    }

    public void updateDomain(Peca existente, AtualizarPecaRequest request) {
        existente.atualizar(request.nome(), request.descricao(), request.valor());
    }

    public PecaResponse toResponse(Peca peca) {
        return PecaResponse.from(peca);
    }
}
