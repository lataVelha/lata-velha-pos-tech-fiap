package br.com.lata.velha.ordem_servico.application.assemblers;

import br.com.lata.velha.ordem_servico.application.dtos.response.PecaAlocadaResponse;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;

public final class PecaAlocadaAssembler {

    private PecaAlocadaAssembler() {
    }

    public static PecaAlocadaResponse toResponse(PecaAlocada pecaAlocada) {
        return PecaAlocadaResponse.from(pecaAlocada);
    }
}
