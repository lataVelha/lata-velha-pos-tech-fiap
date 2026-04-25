package br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque;

import br.com.lata.velha.ordem_servico.application.dtos.request.MovimentarPecaEstoqueRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaEstoqueResponse;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.PecaNotFoundException;
import br.com.lata.velha.ordem_servico.domain.repositories.ExecucaoServicoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaAlocadaRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EntradaPecaEstoqueUseCase {

    private final PecaRepository pecaRepository;
    private final PecaEstoqueRepository pecaEstoqueRepository;
    private final PecaAlocadaRepository pecaAlocadaRepository;
    private final ExecucaoServicoRepository execucaoServicoRepository;

    @Transactional
    public PecaEstoqueResponse execute(Long pecaId, MovimentarPecaEstoqueRequest request) {
        if(!pecaRepository.existsActiveById(pecaId))
            throw PecaNotFoundException.fromId(pecaId);

        var estoque = pecaEstoqueRepository.findByPecaId(pecaId)
                .orElse(PecaEstoque.create(pecaId));

        estoque.adicionar(request.quantidade());
        movimentarReservasPendentes(pecaId, estoque);

        PecaEstoque saved = pecaEstoqueRepository.save(estoque);
        return PecaEstoqueResponse.from(saved);
    }

    private void movimentarReservasPendentes(Long pecaId, PecaEstoque estoque) {
        var pendentes = pecaAlocadaRepository.buscarPendentesPorPecaOrdenado(pecaId);
        if (pendentes.isEmpty()) return;

        var execucaoIds = pendentes.stream()
                .map(PecaAlocada::getExecucaoServicoId)
                .collect(Collectors.toSet());
        var execucoes = execucaoServicoRepository.getAllByIdWithPeca(execucaoIds)
                .stream().collect(Collectors.toMap(
                        ExecucaoServico::getId,
                        execucao -> execucao
                ));

        var modified = new ArrayList<ExecucaoServico>();
        for (var pecaAlocada : pendentes) {
            if (estoque.getQuantidadeDisponivel() <= 0) break;
            var execucao = execucoes.get(pecaAlocada.getExecucaoServicoId());
            execucao.reservarPeca(estoque);
            modified.add(execucao);
        }
        if (!modified.isEmpty())
            execucaoServicoRepository.saveAll(modified);
    }
}