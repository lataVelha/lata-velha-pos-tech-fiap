package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.repositories.*;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RetirarVeiculoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PecaEstoqueRepository pecaEstoqueRepository;
    private final NotificarOrdemServicoUseCase notificarUseCase;
    private final ProprietarioRepository proprietarioRepository;
    private final VeiculoRepository veiculoRepository;

    public OrdemServicoResponse execute(Long idOs, UserId userId) {
        var ordemServico = ordemServicoRepository.getById(idOs);
        var funcionario = funcionarioRepository.getByUserId(userId);

        if (!StatusOrdemServico.FINALIZADA.equals(ordemServico.getStatus())) {
            throw new ResourceAlreadyExistsException(
                    "Esta Ordem de Serviço não foi Finalizada: " + ordemServico.getId()
            );
        }

        processarPecas(ordemServico);

        ordemServico.entregar(funcionario.getId());
        notificarUseCase.execute(ordemServico);

        var saved = ordemServicoRepository.save(ordemServico);
        var proprietario = proprietarioRepository.getActiveById(saved.getProprietarioId());
        var veiculo = veiculoRepository.getActiveById(saved.getVeiculoId());
        String mecanicoNome = saved.getMecanicoResponsavelId() != null
                ? funcionarioRepository.getById(saved.getMecanicoResponsavelId()).getNome()
                : null;

        return OrdemServicoResponse.from(
                saved,
                funcionario.getNome(),
                mecanicoNome,
                proprietario.getNome(),
                veiculo.getMarca() + " " + veiculo.getModelo()
        );
    }

    private void processarPecas(OrdemServico ordemServico) {
        ordemServico.getExecucaoServicos().stream()
                .filter(e -> StatusExecucaoServico.FINALIZADO.equals(e.getStatus()))
                .flatMap(e -> e.getPecas().stream())
                .forEach(pecaAlocada -> {
                    if (!StatusPecaAlocada.INSTALADA.equals(pecaAlocada.getStatus())) {
                        throw new ResourceAlreadyExistsException("Peça não instalada!");
                    }
                    pecaEstoqueRepository.baixarEstoque(
                            pecaAlocada.getPecaId(),
                            pecaAlocada.getQuantidadeSolicitada()
                    );
                });
    }
}