package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarAdminEncomendaPecaService;
import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.shared.domain.value_objects.UserId;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AprovarOrdemServicoUseCase {

    private final AprovarOrdemServicoGateway gateway;
    private final NotificarOrdemServicoService notificarService;
    private final NotificarAdminEncomendaPecaService notificarAdminEncomendaService;

    public AprovarOrdemServicoUseCase(AprovarOrdemServicoGateway gateway,
                                      NotificarOrdemServicoService notificarService,
                                      NotificarAdminEncomendaPecaService notificarAdminEncomendaService) {
        this.gateway = gateway;
        this.notificarService = notificarService;
        this.notificarAdminEncomendaService = notificarAdminEncomendaService;
    }

    public OrdemServico execute(Input input) {
        var ordemServico = gateway.getOrdemServicoComServicosEPecas(input.idOs());
        var funcionario = gateway.getFuncionarioPorUserId(input.userId());

        validateServicos(ordemServico, input.servicos());
        var statusPorId = input.getServiceStatusMap();
        var pecasEstoque = getStockMap(ordemServico.getExecucaoServicos());
        var servicoNomeMap = getServicoNomeMap(ordemServico.getExecucaoServicos());

        ordemServico.getExecucaoServicos().forEach(execucaoServico -> {
            var novoStatus = statusPorId.get(execucaoServico.getId());
            if (novoStatus == null) novoStatus = StatusExecucaoServico.RECUSADO;

            switch (novoStatus) {
                case APROVADO -> {
                    execucaoServico.getPecas().forEach(alocacaoPeca -> {
                        var estoque = pecasEstoque.get(alocacaoPeca.getPecaId());
                        alocacaoPeca.reservar(estoque);

                        if (alocacaoPeca.getQuantidadeEncomendada() != null && alocacaoPeca.getQuantidadeEncomendada() > 0) {
                            notificarAdminEncomendaService.execute(new NotificarAdminEncomendaPecaService.Input(
                                    ordemServico.getId(),
                                    execucaoServico.getId(),
                                    alocacaoPeca.getPecaId(),
                                    alocacaoPeca.getQuantidadeEncomendada(),
                                    servicoNomeMap.get(execucaoServico.getServicoId())
                            ));
                        }
                    });
                    execucaoServico.aprovar(funcionario.getId());
                }
                case RECUSADO -> execucaoServico.recusar(funcionario.getId());
                default -> throw new IllegalArgumentException("Status não suportado: " + novoStatus);
            }
        });

        ordemServico.aprovar(funcionario.getId());
        notificarService.execute(ordemServico);

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
            throw new IllegalArgumentException("Peças não encontradas com Ids: " + idsInvalidos);
        return estoqueMap;
    }

    private void validateServicos(OrdemServico ordemServico, List<Input.ServicoAprovacao> servicos) {
        var registeredIds = ordemServico.getExecucaoServicos().stream()
                .map(ExecucaoServico::getId)
                .collect(Collectors.toSet());
        var idsInvalidos = servicos.stream()
                .filter(servico -> !registeredIds.contains(servico.execucaoServicoId()))
                .toList();
        if (!idsInvalidos.isEmpty())
            throw new IllegalArgumentException("Serviços não pertencem à OS " + ordemServico.getId() + ": " + idsInvalidos);
    }

    private Map<Long, String> getServicoNomeMap(List<ExecucaoServico> execucoes) {
        var servicosIds = execucoes.stream()
                .map(ExecucaoServico::getServicoId)
                .collect(Collectors.toSet());
        var servicos = gateway.getServicosAtivosPorIds(servicosIds).stream()
                .collect(Collectors.toMap(Servico::getId, Servico::getNome));
        if (servicosIds.size() != servicos.size())
            throw new IllegalArgumentException("Alguns serviços solicitados não foram encontrados ou estão inativos");
        return servicos;
    }

    public record Input(Long idOs, UserId userId, List<ServicoAprovacao> servicos) {
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
