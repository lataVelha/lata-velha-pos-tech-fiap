package br.com.lata.velha.application.assembler;

import br.com.lata.velha.application.dto.request.OrdemServicoRequest;
import br.com.lata.velha.application.dto.response.OrdemServicoResponse;
import br.com.lata.velha.domain.model.OrdemServico;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
@Component
public class OrdemServicoAssembler {

    public OrdemServico toDomain(OrdemServicoRequest request) {

        return new OrdemServico(
                null,
                request.getProprietarioId(),
                request.getVeiculoId(),
                request.getReclamacaoCliente(),
                request.getAtendenteInicioId()
        );
    }

    public  OrdemServicoResponse toResponse(
            OrdemServico domain,
            String proprietarioNome,
            String veiculoDescricao
    ) {

        return OrdemServicoResponse.builder()
                .id(domain.getId())

                .veiculoId(domain.getVeiculoId())
                .veiculoDescricao(veiculoDescricao)

                .proprietarioId(domain.getProprietarioId())
                .proprietarioNome(proprietarioNome)

                .status(domain.getStatus().name())

                .iniciadoEm(domain.getIniciadoEm())
                .finalizadoEm(domain.getFinalizadoEm())
                .entregueEm(domain.getEntregueEm())
                .atualizadoEm(domain.getAtualizadoEm())

                .atendenteInicioId(domain.getAtendenteInicioId())
                .mecanicoFinalId(domain.getMecanicoFinalId())

                .servicos(
                        domain.getServicos() != null
                                ? domain.getServicos()
                                .stream()
                                .map(ServicoOSAssembler::toResponse)
                                .collect(Collectors.toList())
                                : null
                )
                .build();
    }
}