package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarAdminEncomendaPecaService;
import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AprovarOrdemServicoUseCase {

    private final AprovarOrdemServicoGateway gateway;
    private final NotificarOrdemServicoService notificarService;
    private final NotificarAdminEncomendaPecaService notificarAdminEncomendaService;
    private final Logger logger;

    public AprovarOrdemServicoUseCase(AprovarOrdemServicoGateway gateway,
                                      NotificarOrdemServicoService notificarService,
                                      NotificarAdminEncomendaPecaService notificarAdminEncomendaService,
                                      Logger logger) {
        this.gateway = gateway;
        this.notificarService = notificarService;
        this.notificarAdminEncomendaService = notificarAdminEncomendaService;
        this.logger = logger;
    }

    public OrdemServico execute(Input input) {
        logger.logInfo("Iniciando aprovação de ordem de serviço - osId={}, quantidadeServicos={}",
                input.idOs(), input.servicos().size());
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
                    logger.logInfo("Aprovando execução de serviço - osId={}, execucaoServicoId={}",
                            ordemServico.getId(), execucaoServico.getId());
                    execucaoServico.getPecas().forEach(alocacaoPeca -> {
                        var estoque = pecasEstoque.get(alocacaoPeca.getPecaId());
                        alocacaoPeca.reservar(estoque);

                        if (alocacaoPeca.getQuantidadeEncomendada() != null && alocacaoPeca.getQuantidadeEncomendada() > 0) {
                            logger.logInfo("Peça com estoque insuficiente, encomendando - pecaId={}, quantidadeEncomendada={}",
                                    alocacaoPeca.getPecaId(), alocacaoPeca.getQuantidadeEncomendada());
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
                case RECUSADO -> {
                    logger.logInfo("Recusando execução de serviço - osId={}, execucaoServicoId={}",
                            ordemServico.getId(), execucaoServico.getId());
                    execucaoServico.recusar(funcionario.getId());
                }
                default -> {
                    logger.logWarn("Aprovação de OS rejeitada: status de execução não suportado - osId={}, execucaoServicoId={}, status={}",
                            ordemServico.getId(), execucaoServico.getId(), novoStatus);
                    throw new IllegalArgumentException("Status não suportado: " + novoStatus);
                }
            }
        });

        ordemServico.aprovar(funcionario.getId());
        notificarService.execute(ordemServico);

        gateway.salvarEstoques(pecasEstoque.values());
        var saved = gateway.salvarOrdemServico(ordemServico);
        logger.logInfo("Aprovação de ordem de serviço concluída com sucesso - osId={}, status={}", saved.getId(), saved.getStatus());
        return saved;
    }

    private Map<Long, PecaEstoque> getStockMap(List<ExecucaoServico> execucaoServicos) {
        var pecaIds = execucaoServicos.stream()
                .flatMap(s -> s.getPecas().stream().map(PecaAlocada::getPecaId))
                .collect(Collectors.toSet());
        var estoqueMap = gateway.getEstoquePorPecaIds(pecaIds).stream()
                .collect(Collectors.toMap(PecaEstoque::getPecaId, p -> p));
        var idsInvalidos = new HashSet<>(pecaIds);
        idsInvalidos.removeAll(estoqueMap.keySet());
        if (!idsInvalidos.isEmpty()) {
            logger.logWarn("Aprovação de OS rejeitada: peças sem registro de estoque - pecaIds={}", idsInvalidos);
            throw new IllegalArgumentException("Peças não encontradas com Ids: " + idsInvalidos);
        }
        return estoqueMap;
    }

    private void validateServicos(OrdemServico ordemServico, List<Input.ServicoAprovacao> servicos) {
        var registeredIds = ordemServico.getExecucaoServicos().stream()
                .map(ExecucaoServico::getId)
                .collect(Collectors.toSet());
        var idsInvalidos = servicos.stream()
                .filter(servico -> !registeredIds.contains(servico.execucaoServicoId()))
                .toList();
        if (!idsInvalidos.isEmpty()) {
            logger.logWarn("Aprovação de OS rejeitada: serviços não pertencem à OS - osId={}, execucaoServicoIds={}",
                    ordemServico.getId(), idsInvalidos);
            throw new IllegalArgumentException("Serviços não pertencem à OS " + ordemServico.getId() + ": " + idsInvalidos);
        }
    }

    private Map<Long, String> getServicoNomeMap(List<ExecucaoServico> execucoes) {
        var servicosIds = execucoes.stream()
                .map(ExecucaoServico::getServicoId)
                .collect(Collectors.toSet());
        var servicos = gateway.getServicosAtivosPorIds(servicosIds).stream()
                .collect(Collectors.toMap(Servico::getId, Servico::getNome));
        if (servicosIds.size() != servicos.size()) {
            logger.logWarn("Aprovação de OS rejeitada: serviços não encontrados ou inativos - servicoIdsSolicitados={}, servicoIdsEncontrados={}",
                    servicosIds, servicos.keySet());
            throw new IllegalArgumentException("Alguns serviços solicitados não foram encontrados ou estão inativos");
        }
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
