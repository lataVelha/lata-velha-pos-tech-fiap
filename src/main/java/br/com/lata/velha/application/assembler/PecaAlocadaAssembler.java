package br.com.lata.velha.application.assembler;

import br.com.lata.velha.application.dto.response.PecaAlocadaResponse;
import br.com.lata.velha.domain.model.PecaAlocada;

public class PecaAlocadaAssembler {

    public static PecaAlocadaResponse toResponse(PecaAlocada domain) {
        return new PecaAlocadaResponse(
                domain.getId(),
                domain.getPecaId(),
                null, // For pecaNome, we need another query if the client needs it. Returning null.
                domain.getQuantidadeAlocada(),
                domain.getServicoOsId()
        );
    }
}