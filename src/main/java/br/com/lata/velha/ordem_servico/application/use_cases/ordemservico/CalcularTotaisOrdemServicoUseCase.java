package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.TotaisOrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.shared.application.logging.Logger;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class CalcularTotaisOrdemServicoUseCase {

    private static final Set<StatusExecucaoServico> STATUS_APROVADOS = Set.of(
            StatusExecucaoServico.APROVADO,
            StatusExecucaoServico.EM_EXECUCAO,
            StatusExecucaoServico.AGUARDANDO_PECA,
            StatusExecucaoServico.FINALIZADO
    );

    private final Logger logger;

    public CalcularTotaisOrdemServicoUseCase(Logger logger) {
        this.logger = logger;
    }

    public TotaisOrdemServicoResponse execute(List<ExecucaoServico> execucoes) {
        logger.logInfo("Calculando totais de ordem de serviço - quantidadeExecucoes={}", execucoes.size());
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
