package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import br.com.lata.velha.shared.domain.value_objects.UserId;

import java.math.BigDecimal;
import java.util.List;

public class CriarOrdemServicoUseCase {

    private final CriarOrdemServicoGateway gateway;
    private final AdicionarServicoUseCase adicionarServicoUseCase;
    private final NotificarOrdemServicoUseCase notificarUseCase;

    public CriarOrdemServicoUseCase(CriarOrdemServicoGateway gateway,
                                    AdicionarServicoUseCase adicionarServicoUseCase,
                                    NotificarOrdemServicoUseCase notificarUseCase) {
        this.gateway = gateway;
        this.adicionarServicoUseCase = adicionarServicoUseCase;
        this.notificarUseCase = notificarUseCase;
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

        if (input.sevicoId() != null && input.pecaId() != null && input.valorMaoDeObra() != null) {
            adicionarServicoUseCase.execute(new AdicionarServicoUseCase.Input(
                    saved.getId(), List.of(new AdicionarServicoUseCase.Input.ServicoAdicionar(
                    input.sevicoId(), List.of(new AdicionarServicoUseCase.Input.PecaNecessaria(input.pecaId(), input.quantidade())), input.valorMaoDeObra()))));
        }

        notificarUseCase.execute(saved);

        return gateway.getOrdemServicoProjectionById(saved.getId());
    }

    public record Input(Long veiculoId, Long proprietarioId, UserId userId, String reclamacaoProprietario,
                        Long pecaId, Integer quantidade, Long sevicoId, BigDecimal valorMaoDeObra) {}
}
