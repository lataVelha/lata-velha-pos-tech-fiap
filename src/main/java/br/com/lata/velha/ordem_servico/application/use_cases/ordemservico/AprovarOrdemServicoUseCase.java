package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.TotaisOrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AprovarOrdemServicoUseCase {
    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PecaEstoqueRepository pecaEstoqueRepository;
    private final NotificarOrdemServicoUseCase notificarUseCase;
    private final CalcularTotaisOrdemServicoUseCase calcularTotaisUseCase;
    private final NotificarAdminEncomendaPecaUseCase notificarAdminEncomendaUseCase;

    @Transactional
    public Output execute(Input input) {
        var ordemServico = ordemServicoRepository.getByIdWithExecucoes(input.idOs());
        var funcionario = funcionarioRepository.getByUserId(input.userId());

        var statusPorId = input.getServiceStatusMap();

        var idsValidos = ordemServico.getExecucaoServicos().stream()
                .map(ExecucaoServico::getId)
                .collect(Collectors.toSet());
        var idsInvalidos = statusPorId.keySet().stream()
                .filter(id -> !idsValidos.contains(id))
                .toList();
        if (!idsInvalidos.isEmpty()) {
            throw new IllegalArgumentException("Serviços não pertencem à OS " + input.idOs() + ": " + idsInvalidos);
        }

        var pecasEstoque = getStockMap(ordemServico.getExecucaoServicos());

        ordemServico.getExecucaoServicos().forEach(execucaoServico -> {
            var novoStatus = statusPorId.get(execucaoServico.getId());
            if (novoStatus == null) novoStatus = StatusExecucaoServico.RECUSADO;

            switch (novoStatus) {
                case APROVADO -> {
                    execucaoServico.aprovar(funcionario.getId());
                    execucaoServico.getPecas().forEach(alocacaoPeca -> {
                        var estoque = pecasEstoque.get(alocacaoPeca.getPecaId());
                        alocacaoPeca.reservar(estoque);

                        if (alocacaoPeca.getQuantidadeEncomendada() != null && alocacaoPeca.getQuantidadeEncomendada() > 0) {
                            notificarAdminEncomendaUseCase.execute(new NotificarAdminEncomendaPecaUseCase.Input(
                                    ordemServico.getId(),
                                    execucaoServico.getId(),
                                    alocacaoPeca.getPecaId(),
                                    alocacaoPeca.getQuantidadeEncomendada(),
                                    execucaoServico.getServico().getNome()
                            ));
                        }
                    });
                }

                case RECUSADO -> execucaoServico.recusar(funcionario.getId());
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
                .flatMap(s -> s.getPecas()
                        .stream()
                        .map(PecaAlocada::getPecaId))
                .collect(Collectors.toSet());
        List<PecaEstoque> estoque = pecaEstoqueRepository.findAllByPecaIds(pecaIds);
        return estoque.stream()
                .collect(Collectors
                                .toMap(PecaEstoque::getPecaId, p -> p)
                );
    }

    public record Input(Long idOs, UUID userId, List<Servicos> servicos) {
        public Map<Long, StatusExecucaoServico> getServiceStatusMap() {
            return servicos.stream()
                    .collect(Collectors.toMap(
                            Servicos::servicoOsId,
                            Servicos::status
                    ));
        }

        public record Servicos(Long servicoOsId, StatusExecucaoServico status) {}
    }

    public record Output(Long idOs, String status, List<Servico> servicos, TotaisOrdemServicoResponse totais) {
        public record Servico(Long idServicoOs, String statusServico) {}
    }
}
