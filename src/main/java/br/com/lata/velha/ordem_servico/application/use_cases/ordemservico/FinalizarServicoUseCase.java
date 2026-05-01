package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FinalizarServicoUseCase {
    private final OrdemServicoRepository ordemServicoRepository;
    private final PecaEstoqueRepository pecaEstoqueRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final NotificarOrdemServicoUseCase notificarUseCase;

    @Transactional
    public void execute(Input input) {
        var ordemServico = ordemServicoRepository.getByIdWithExecucoesAndPecas(input.osId());
        var mecanico = funcionarioRepository.getByUserId(input.userId());
        ordemServico.finalizarExecucaoServico(input.servicoId(), mecanico.getId());
        retirarEstoques(ordemServico, input.servicoId());
        var saved = ordemServicoRepository.save(ordemServico);
        if (ordemServico.isFinalizada())
            notificarUseCase.execute(saved);
    }

    private void retirarEstoques(OrdemServico ordemServico, Long servicoId) {
        var execucaoFinalizada = ordemServico.getExecucaoById(servicoId);
        var quantidadesMap = execucaoFinalizada.getPecas().stream()
                .collect(Collectors.toMap(
                        PecaAlocada::getPecaId,
                        PecaAlocada::getQuantidadeSolicitada
                ));
        var estoques = pecaEstoqueRepository.findAllByPecaIds(quantidadesMap.keySet());
        if(estoques.size() != quantidadesMap.size())
            throw new IllegalStateException("Nem todas as peças da execução possuem registro de estoque");
        estoques.forEach(estoque -> estoque.retirar(quantidadesMap.get(estoque.getPecaId())));
        pecaEstoqueRepository.saveAll(estoques);
    }

    public record Input(Long osId, Long servicoId, UserId userId){}
}
