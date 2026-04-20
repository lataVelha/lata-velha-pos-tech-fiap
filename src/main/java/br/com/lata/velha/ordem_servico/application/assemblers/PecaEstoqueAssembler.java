package br.com.lata.velha.ordem_servico.application.assemblers;

import br.com.lata.velha.ordem_servico.application.dtos.response.PecaEstoqueResponse;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import org.springframework.stereotype.Component;

@Component
public class PecaEstoqueAssembler {

    public PecaEstoqueResponse toResponse(PecaEstoque estoque) {
        return PecaEstoqueResponse.from(estoque);
    }
}
