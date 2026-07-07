package br.com.lata.velha.ordem_servico.api.presenters.ordemservico;

import br.com.lata.velha.ordem_servico.api.dtos.ordem_servico.AprovarOrdemServicoResponse;
import br.com.lata.velha.ordem_servico.application.dtos.response.TotaisOrdemServicoResponse;
import br.com.lata.velha.ordem_servico.application.presenters.ordemservico.AprovarOrdemServicoPresenter;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
public class AprovarOrdemServicoPresenterImpl implements AprovarOrdemServicoPresenter {

    private static final Set<StatusExecucaoServico> STATUS_APROVADOS = Set.of(
            StatusExecucaoServico.APROVADO,
            StatusExecucaoServico.EM_EXECUCAO,
            StatusExecucaoServico.AGUARDANDO_PECA,
            StatusExecucaoServico.FINALIZADO
    );

    @Override
    public AprovarOrdemServicoResponse present(OrdemServico ordemServico) {
        List<AprovarOrdemServicoResponse.Servico> servicos = ordemServico.getExecucaoServicos().stream()
                .map(s -> new AprovarOrdemServicoResponse.Servico(s.getId(), s.getStatus().name()))
                .toList();

        TotaisOrdemServicoResponse totais = calcularTotais(ordemServico.getExecucaoServicos());

        return new AprovarOrdemServicoResponse(
                ordemServico.getId(),
                ordemServico.getStatus().name(),
                servicos,
                totais
        );
    }

    private TotaisOrdemServicoResponse calcularTotais(List<ExecucaoServico> execucoes) {
        if (execucoes == null || execucoes.isEmpty()) return null;

        BigDecimal maoDeObraAprovada = execucoes.stream()
                .filter(s -> STATUS_APROVADOS.contains(s.getStatus()))
                .map(s -> s.getValorMaoDeObra() != null ? s.getValorMaoDeObra() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pecasAprovadas = execucoes.stream()
                .filter(s -> STATUS_APROVADOS.contains(s.getStatus()))
                .flatMap(s -> s.getPecas() != null ? s.getPecas().stream() : java.util.stream.Stream.empty())
                .map(p -> {
                    BigDecimal valor = p.getValorUnitarioPeca() != null ? p.getValorUnitarioPeca() : BigDecimal.ZERO;
                    int qtd = p.getQuantidadeSolicitada() != null ? p.getQuantidadeSolicitada() : 0;
                    return valor.multiply(BigDecimal.valueOf(qtd));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRecusado = execucoes.stream()
                .filter(s -> s.getStatus() == StatusExecucaoServico.RECUSADO)
                .map(s -> s.getValorMaoDeObra() != null ? s.getValorMaoDeObra() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TotaisOrdemServicoResponse(
                maoDeObraAprovada,
                pecasAprovadas,
                maoDeObraAprovada.add(pecasAprovadas),
                totalRecusado
        );
    }
}
