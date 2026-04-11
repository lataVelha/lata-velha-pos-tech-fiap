package br.com.lata.velha.application.assembler;

import br.com.lata.velha.application.dto.request.ServicoOSRequest;
import br.com.lata.velha.application.dto.response.ServicoOSResponse;
import br.com.lata.velha.domain.model.Servico;
import br.com.lata.velha.domain.model.ServicoOS;

public class ServicoOSAssembler {

    public static ServicoOS toDomain(ServicoOSRequest request) {

        Servico servico = new Servico();
        servico.setId(request.servicoId());

        return new ServicoOS(
                servico,
                request.valorMaoDeObra()
        );
    }

    public static ServicoOSResponse toResponse(ServicoOS domain) {

        return new ServicoOSResponse(
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