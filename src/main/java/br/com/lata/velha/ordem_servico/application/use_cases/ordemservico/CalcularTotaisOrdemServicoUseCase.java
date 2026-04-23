package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.TotaisOrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
public class CalcularTotaisOrdemServicoUseCase {

    private static final Set<StatusExecucaoServico> STATUS_APROVADOS = Set.of(
            StatusExecucaoServico.APROVADO,
            StatusExecucaoServico.EM_EXECUCAO,
            StatusExecucaoServico.AGUARDANDO_PECA,
            StatusExecucaoServico.FINALIZADO
    );

    public TotaisOrdemServicoResponse execute(List<ExecucaoServico> execucoes) {
        var maoDeObraAprovada = execucoes.stream()
                .filter(e -> STATUS_APROVADOS.contains(e.getStatus()))
                .map(ExecucaoServico::getValorMaoDeObra)
                .map(v -> v != null ? v : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var pecasAprovadas = execucoes.stream()
                .filter(e -> STATUS_APROVADOS.contains(e.getStatus()))
                .flatMap(e -> e.getPecas().stream())
                .map(peca -> peca.getValorUnitarioPeca().multiply(BigDecimal.valueOf(peca.getQuantidadeSolicitada())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var totalRecusado = execucoes.stream()
                .filter(e -> e.getStatus() == StatusExecucaoServico.RECUSADO)
                .map(e -> {
                    var mdo = e.getValorMaoDeObra() != null ? e.getValorMaoDeObra() : BigDecimal.ZERO;
                    var pecas = e.getPecas().stream()
                            .map(peca -> peca.getValorUnitarioPeca().multiply(BigDecimal.valueOf(peca.getQuantidadeSolicitada())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return mdo.add(pecas);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TotaisOrdemServicoResponse(
                maoDeObraAprovada,
                pecasAprovadas,
                maoDeObraAprovada.add(pecasAprovadas),
                totalRecusado
        );
    }
}
