package br.com.lata.velha.ordemDeServico.application.assemblers;

import br.com.lata.velha.ordemDeServico.application.dtos.response.PecaAlocadaResponse;
import br.com.lata.velha.ordemDeServico.domain.entities.PecaAlocada;

public class PecaAlocadaAssembler {

    public static PecaAlocadaResponse toResponse(PecaAlocada domain) {
        return new PecaAlocadaResponse(
                domain.getId(),
                domain.getPecaId(),
                null, // pecaNome (buscar depois se precisar)
                domain.getQuantidadeSolicitada(),
                domain.getQuantidadeSolicitada(),
                domain.getQuantidadeReservada(),
                domain.getQuantidadeEncomendada(),
                domain.getStatus(),
                domain.getServicoOsId()
        );
    }
}