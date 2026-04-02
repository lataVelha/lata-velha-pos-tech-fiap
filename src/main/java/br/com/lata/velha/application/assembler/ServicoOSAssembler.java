package br.com.lata.velha.application.assembler;

import br.com.lata.velha.application.dto.request.ServicoOSRequest;
import br.com.lata.velha.application.dto.response.ServicoOSResponse;
import br.com.lata.velha.domain.model.Servico;
import br.com.lata.velha.domain.model.ServicoOS;

public class ServicoOSAssembler {

    public static ServicoOS toDomain(ServicoOSRequest request) {

        Servico servico = new Servico();
        servico.setId(request.getServicoId());

        return new ServicoOS(
                null,
                servico,
                request.getValorMaoDeObra()
        );
    }

    public static ServicoOSResponse toResponse(ServicoOS domain) {

        return ServicoOSResponse.builder()
                .id(domain.getId())
                .servicoId(domain.getServico().getId())
                .servicoNome(domain.getServico().getNome())
                .status(domain.getStatus().name())
                .mecanicoResponsavelId(domain.getMecanicoResponsavelId())
                .valorMaoDeObra(domain.getValorMaoDeObra())
                .iniciadoEm(domain.getIniciadoEm())
                .terminadoEm(domain.getTerminadoEm())
                .atualizadoEm(domain.getAtualizadoEm())
                .build();
    }
}