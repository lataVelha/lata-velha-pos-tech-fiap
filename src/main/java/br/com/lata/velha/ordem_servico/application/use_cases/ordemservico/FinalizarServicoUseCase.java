package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.shared.domain.value_objects.UserId;

import java.util.stream.Collectors;

public class FinalizarServicoUseCase {

    private final FinalizarServicoGateway gateway;
    private final NotificarOrdemServicoUseCase notificarUseCase;

    public FinalizarServicoUseCase(FinalizarServicoGateway gateway,
                                   NotificarOrdemServicoUseCase notificarUseCase) {
        this.gateway = gateway;
        this.notificarUseCase = notificarUseCase;
    }

    public void execute(Input input) {
        var ordemServico = gateway.getOrdemServicoComServicosEPecas(input.osId());
        var mecanico = gateway.getFuncionarioPorUserId(input.userId());
        ordemServico.finalizarExecucaoServico(input.servicoId(), mecanico.getId());
        retirarEstoques(ordemServico, input.servicoId());
        var saved = gateway.salvarOrdemServico(ordemServico);
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
        var estoques = gateway.getEstoquePorPecaIds(quantidadesMap.keySet());
        if (estoques.size() != quantidadesMap.size())
            throw new IllegalStateException("Nem todas as peças da execução possuem registro de estoque");
        estoques.forEach(estoque -> estoque.retirar(quantidadesMap.get(estoque.getPecaId())));
        gateway.salvarEstoques(estoques);
    }

    public record Input(Long osId, Long servicoId, UserId userId) {}
}
