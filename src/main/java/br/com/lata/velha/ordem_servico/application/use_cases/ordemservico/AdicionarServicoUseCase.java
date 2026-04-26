package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.ServicoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AdicionarServicoUseCase {
    private final OrdemServicoRepository ordemServicoRepository;
    private final ServicoRepository servicoRepository;
    private final PecaRepository pecaRepository;

    @Transactional
    public void execute(Input input) {
        var ordemServico = ordemServicoRepository.getById(input.osId);
        var execucoes = createExecucoes(input.servicos(), ordemServico.getId());
        execucoes.forEach(ordemServico::adicionarServico);
        ordemServico = ordemServicoRepository.save(ordemServico);
        var idsSolicitados = input.servicos.stream().map(Input.ServicoAdicionar::servicoId).collect(Collectors.toSet());
        execucoes = ordemServico.getExecucaoServicos().stream()
                .filter(execucaoServico -> idsSolicitados.stream().anyMatch(id -> execucaoServico.getServicoId().equals(id)))
                .toList();
        adicionarPecas(execucoes, input.servicos);
        ordemServicoRepository.save(ordemServico);
    }

    private List<ExecucaoServico> createExecucoes(List<Input.ServicoAdicionar> servicosAdicionar, Long osId) {
        Set<Long> servicoIds = servicosAdicionar.stream().map(Input.ServicoAdicionar::servicoId).collect(Collectors.toSet());
        validateDuplicates(servicoIds, servicosAdicionar);
        var servicos = servicoRepository.getAllActiveById(servicoIds);
        validateIds(servicoIds, servicos);
        var servicoAdicionarMap = servicosAdicionar.stream().collect(Collectors.toMap(
                Input.ServicoAdicionar::servicoId,
                servico -> servico
        ));
        return servicoAdicionarMap.entrySet().stream()
                .map(entry -> {
                    var value = entry.getValue();
                    return ExecucaoServico.create(entry.getKey(), osId, value.valorMaoDeObra);
                })
                .toList();
    }

    private void adicionarPecas(List<ExecucaoServico> execucoes, List<Input.ServicoAdicionar> servicos) {
        var pecasPrecos = getPecasPrecoMap(servicos);
        var pecasMap = servicos.stream()
                .collect(Collectors.toMap(
                        Input.ServicoAdicionar::servicoId,
                        Input.ServicoAdicionar::pecas
                ));
        execucoes.forEach(execucao -> {
            var pecas = pecasMap.get(execucao.getServicoId());
            pecas.stream()
                    .map(peca -> PecaAlocada.create(peca.pecaId(), execucao.getId(), pecasPrecos.get(peca.pecaId()), peca.quantidade()))
                    .forEach(execucao::adicionarPeca);
        });
    }

    private Map<Long, BigDecimal> getPecasPrecoMap(List<Input.ServicoAdicionar> servicos) {
        var pecasIds = servicos.stream()
                .map(Input.ServicoAdicionar::pecas)
                .flatMap(List::stream)
                .map(Input.PecaNecessaria::pecaId)
                .collect(Collectors.toSet());
        var valores = pecaRepository.getAllActiveByIds(pecasIds).stream()
                .collect(Collectors.toMap(
                        Peca::getId,
                        Peca::getValor
                ));
        if(pecasIds.size() != valores.size())
            throw new IllegalArgumentException("Algumas pecas informadas não existem!");
        return valores;
    }

    private void validateDuplicates(Set<Long> servicoIds, List<Input.ServicoAdicionar> servicosAdicionar) {
        if(servicoIds.size() != servicosAdicionar.size())
            throw new IllegalArgumentException("Não é possível inserir serviços duplicados!");
    }

    private void validateIds(Set<Long> servicoIds, Set<Servico> servicos) {
        if(servicos.size() == 0)
            throw new IllegalArgumentException("Nenhum serviço solicitado existe");
        var invalidIds = servicos.stream()
                .map(Servico::getId)
                .filter(id -> servicoIds.stream().noneMatch(servicoId -> servicoId.equals(id)))
                .collect(Collectors.toSet());
        if(!invalidIds.isEmpty())
            throw new IllegalArgumentException("Servicos com Ids: " + invalidIds + " não existem!");
    }

    public record Input(Long osId, List<ServicoAdicionar> servicos){
        public record ServicoAdicionar(Long servicoId, List<PecaNecessaria> pecas, BigDecimal valorMaoDeObra) { }
        public record PecaNecessaria(Long pecaId, Integer quantidade) {}
    }
}
