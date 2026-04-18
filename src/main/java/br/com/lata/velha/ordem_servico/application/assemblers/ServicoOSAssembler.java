package br.com.lata.velha.ordem_servico.application.assemblers;

import br.com.lata.velha.ordem_servico.application.dtos.request.ServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.ExecucaoServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;

public class ServicoOSAssembler {

    public static ExecucaoServico toDomain(ServicoRequest request) {

        Servico servico = new Servico();
        servico.setId(request.servicoId());

        return new ExecucaoServico(
                servico,
                request.valorMaoDeObra()
        );
    }

    public static ExecucaoServicoResponse toResponse(ExecucaoServico domain) {

        return new ExecucaoServicoResponse(
                domain.getId(),
                domain.getServico().getId(),
                domain.getServico().getNome(),
                domain.getStatus().name(),
                domain.getMecanicoResponsavelId(),
                domain.getValorMaoDeObra(),
                domain.getIniciadoEm(),
                domain.getTerminadoEm(),
                domain.getAtualizadoEm(),
                null // pecas
        );
    }
}