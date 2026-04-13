package br.com.lata.velha.application.assembler;

import br.com.lata.velha.application.dto.response.PecaEstoqueResponse;
import br.com.lata.velha.domain.entities.PecaEstoque;
import org.springframework.stereotype.Component;

@Component
public class PecaEstoqueAssembler {

    public PecaEstoqueResponse toResponse(PecaEstoque model) {
        return new PecaEstoqueResponse(model.getPecaId(), model.getQuantidadeArmazenada());
    }
}
