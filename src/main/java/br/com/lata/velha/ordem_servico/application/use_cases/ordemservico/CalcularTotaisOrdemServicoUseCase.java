package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.TotaisOrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CalcularTotaisOrdemServicoUseCase {

    private final PecaRepository pecaRepository;

    public TotaisOrdemServicoResponse execute(List<ExecucaoServico> execucoes) {
        var pecaIds = execucoes.stream()
                .filter(e -> e.getStatus() == StatusExecucaoServico.APROVADO)
                .flatMap(e -> e.getPecas().stream().map(PecaAlocada::getPecaId))
                .collect(Collectors.toSet());

        Map<Long, Peca> pecasPorId = pecaIds.isEmpty()
                ? Map.of()
                : pecaRepository.findAllByIds(pecaIds).stream()
                        .collect(Collectors.toMap(Peca::getId, p -> p));

        var maoDeObraAprovada = execucoes.stream()
                .filter(e -> e.getStatus() == StatusExecucaoServico.APROVADO)
                .map(ExecucaoServico::getValorMaoDeObra)
                .map(v -> v != null ? v : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var pecasAprovadas = execucoes.stream()
                .filter(e -> e.getStatus() == StatusExecucaoServico.APROVADO)
                .flatMap(e -> e.getPecas().stream())
                .map(peca -> {
                    var p = pecasPorId.get(peca.getPecaId());
                    if (p == null) return BigDecimal.ZERO;
                    return p.getValor().multiply(BigDecimal.valueOf(peca.getQuantidadeSolicitada()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var totalRecusado = execucoes.stream()
                .filter(e -> e.getStatus() == StatusExecucaoServico.RECUSADO)
                .map(ExecucaoServico::getValorMaoDeObra)
                .map(v -> v != null ? v : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TotaisOrdemServicoResponse(
                maoDeObraAprovada,
                pecasAprovadas,
                maoDeObraAprovada.add(pecasAprovadas),
                totalRecusado
        );
    }
}
