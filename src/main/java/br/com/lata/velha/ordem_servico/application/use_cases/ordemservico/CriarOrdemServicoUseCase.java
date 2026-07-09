package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import br.com.lata.velha.shared.domain.value_objects.UserId;

public class CriarOrdemServicoUseCase {

    private final CriarOrdemServicoGateway gateway;
    private final NotificarOrdemServicoService notificarService;

    public CriarOrdemServicoUseCase(CriarOrdemServicoGateway gateway,
                                    NotificarOrdemServicoService notificarService) {
        this.gateway = gateway;
        this.notificarService = notificarService;
    }

    public OrdemServicoProjection execute(Input input) {
        var proprietario = gateway.getProprietarioAtivoPorId(input.proprietarioId());
        var veiculo = gateway.getVeiculoAtivoDoProprietario(input.veiculoId(), proprietario.getId());
        var funcionario = gateway.getFuncionarioPorUserId(input.userId());

        var ordemServico = OrdemServico.create(
                proprietario.getId(),
                veiculo.getId(),
                input.reclamacaoProprietario(),
                funcionario.getId()
        );
        var saved = gateway.salvarOrdemServico(ordemServico);

        notificarService.execute(saved);

        return gateway.getOrdemServicoProjectionById(saved.getId());
    }

    public record Input(Long veiculoId, Long proprietarioId, UserId userId, String reclamacaoProprietario) {}
}
