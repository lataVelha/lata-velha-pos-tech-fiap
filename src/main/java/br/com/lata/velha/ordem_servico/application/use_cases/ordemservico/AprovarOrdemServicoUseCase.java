package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.TotaisOrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.*;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.ServicoRepository;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AprovarOrdemServicoUseCase {
    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PecaEstoqueRepository pecaEstoqueRepository;
    private final ServicoRepository servicoRepository;
    private final NotificarOrdemServicoUseCase notificarUseCase;
    private final CalcularTotaisOrdemServicoUseCase calcularTotaisUseCase;
    private final NotificarAdminEncomendaPecaUseCase notificarAdminEncomendaUseCase;

    @Transactional
    public Output execute(Input input) {
        var ordemServico = ordemServicoRepository.getByIdWithExecucoesAndPecas(input.idOs());
        var funcionario = funcionarioRepository.getByUserId(input.userId());

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
                            notificarAdminEncomendaUseCase.execute(new NotificarAdminEncomendaPecaUseCase.Input(
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

                default -> throw new IllegalArgumentException(
                        "Status não suportado: " + novoStatus
                );
            }
        });

        ordemServico.aprovar(funcionario.getId());
        notificarUseCase.execute(ordemServico);

        pecaEstoqueRepository.saveAll(pecasEstoque.values());
        var saved = ordemServicoRepository.save(ordemServico);

        List<Output.Servico> servicos = saved.getExecucaoServicos().stream()
                .map(s -> new Output.Servico(s.getId(), s.getStatus().name()))
                .toList();

        var totais = calcularTotaisUseCase.execute(saved.getExecucaoServicos());

        return new Output(saved.getId(), saved.getStatus().name(), servicos, totais);
    }

    private Map<Long, PecaEstoque> getStockMap(List<ExecucaoServico> execucaoServicos) {
        var pecaIds = execucaoServicos.stream()
                .flatMap(s -> s.getPecas().stream()
                        .map(PecaAlocada::getPecaId))
                .collect(Collectors.toSet());
        var estoqueMap = pecaEstoqueRepository.findAllByPecaIds(pecaIds).stream()
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
        var servicos = servicoRepository.getAllActiveById(servicosIds)
                .stream()
                .collect(Collectors.toMap(
                        Servico::getId,
                        Servico::getNome
                ));
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

        public record ServicoAprovacao(Long execucaoServicoId, StatusExecucaoServico status) {
        }
    }

    public record Output(Long idOs, String status, List<Servico> servicos, TotaisOrdemServicoResponse totais) {
        public record Servico(Long idServicoOs, String statusServico) {
        }
    }
}
