package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReceberAprovacaoOrcamentoClienteUseCase {

    private final ReceberAprovacaoOrcamentoClienteGateway gateway;

    public ReceberAprovacaoOrcamentoClienteUseCase(ReceberAprovacaoOrcamentoClienteGateway gateway) {
        this.gateway = gateway;
    }

    public OrdemServico execute(Input input) {
        var ordemServico = gateway.getOrdemServicoComServicosEPecas(input.osId());
        validateServicos(ordemServico, input.servicos());

        var statusPorId = input.getServiceStatusMap();
        var pecasEstoque = getStockMap(ordemServico.getExecucaoServicos());

        ordemServico.getExecucaoServicos().forEach(execucaoServico -> {
            var novoStatus = statusPorId.get(execucaoServico.getId());
            if (novoStatus == StatusExecucaoServico.APROVADO) {
                execucaoServico.getPecas().forEach(alocacaoPeca -> {
                    var estoque = pecasEstoque.get(alocacaoPeca.getPecaId());
                    alocacaoPeca.reservar(estoque);
                });
                execucaoServico.aprovar(null);
            } else {
                execucaoServico.recusar(null);
            }
        });

        boolean todosRecusados = ordemServico.getExecucaoServicos().stream()
                .allMatch(ExecucaoServico::isRecusado);
        if (todosRecusados) {
            ordemServico.reprovar(null);
        } else {
            ordemServico.aprovar(null);
        }

        gateway.salvarEstoques(pecasEstoque.values());
        return gateway.salvarOrdemServico(ordemServico);
    }

    private Map<Long, PecaEstoque> getStockMap(List<ExecucaoServico> execucaoServicos) {
        var pecaIds = execucaoServicos.stream()
                .flatMap(s -> s.getPecas().stream().map(PecaAlocada::getPecaId))
                .collect(Collectors.toSet());
        var estoqueMap = gateway.getEstoquePorPecaIds(pecaIds).stream()
                .collect(Collectors.toMap(PecaEstoque::getPecaId, p -> p));
        var idsInvalidos = new HashSet<>(pecaIds);
        idsInvalidos.removeAll(estoqueMap.keySet());
        if (!idsInvalidos.isEmpty())
            throw new IllegalArgumentException("Peças sem registro de estoque: " + idsInvalidos);
        return estoqueMap;
    }

    private void validateServicos(OrdemServico ordemServico, List<Input.ServicoAprovacao> servicos) {
        var registeredIds = ordemServico.getExecucaoServicos().stream()
                .map(ExecucaoServico::getId)
                .collect(Collectors.toSet());
        var idsInvalidos = servicos.stream()
                .filter(s -> !registeredIds.contains(s.execucaoServicoId()))
                .toList();
        if (!idsInvalidos.isEmpty())
            throw new IllegalArgumentException("Serviços não pertencem à OS " + ordemServico.getId() + ": " + idsInvalidos);
    }

    public record Input(Long osId, List<ServicoAprovacao> servicos) {
        public Map<Long, StatusExecucaoServico> getServiceStatusMap() {
            return servicos.stream()
                    .collect(Collectors.toMap(
                            ServicoAprovacao::execucaoServicoId,
                            ServicoAprovacao::status
                    ));
        }

        public record ServicoAprovacao(Long execucaoServicoId, StatusExecucaoServico status) {}
    }
}
