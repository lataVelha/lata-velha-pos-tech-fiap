package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.assemblers.OrdemServicoAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.repositories.*;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class RetirarVeiculoUseCase {

    private final OrdemServicoAssembler ordemServicoAssembler;
    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PecaEstoqueRepository pecaEstoqueRepository;
    private final PecaRepository pecaRepository;

    public OrdemServicoResponse execute(Long idOs, Long idFuncionario) {

        var ordemServico = ordemServicoRepository.findById(idOs);
        var funcionario = funcionarioRepository.getById(idFuncionario);

        if (!StatusOrdemServico.FINALIZADA.equals(ordemServico.getStatus())) {
            throw new ResourceAlreadyExistsException(
                    "Esta Ordem de Serviço não foi Finalizada: " + ordemServico.getId()
            );
        }

        BigDecimal totalServicos = totalServicos(ordemServico);
        BigDecimal totalPecas = totalPecas(ordemServico);
        BigDecimal totalOrdemServico = totalServicos.add(totalPecas);

        ordemServico.entregar(funcionario.getId());

        return ordemServicoAssembler.toResponse(
                ordemServicoRepository.save(ordemServico),
                null,
                null,
                totalServicos,
                totalPecas,
                totalOrdemServico
        );
    }

    private BigDecimal totalServicos(OrdemServico ordemServico) {

        return ordemServico.getExecucaoServicos().stream()
                .filter(execucaoServico ->
                        StatusExecucaoServico.FINALIZADO.equals(execucaoServico.getStatus())
                )
                .map(execucaoServico ->
                        execucaoServico.getValorMaoDeObra() != null
                                ? execucaoServico.getValorMaoDeObra()
                                : BigDecimal.ZERO
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal totalPecas(OrdemServico ordemServico) {

        return ordemServico.getExecucaoServicos().stream()
                .filter(execucaoServico ->
                        StatusExecucaoServico.FINALIZADO.equals(execucaoServico.getStatus())
                )
                .flatMap(execucaoServico ->
                        execucaoServico.getPecas().stream()
                                .map(pecaAlocada -> {

                                    if (!StatusPecaAlocada.INSTALADA.equals(pecaAlocada.getStatus())) {
                                        throw new ResourceAlreadyExistsException("Peça não instalada!");
                                    }

                                    Integer quantidade = pecaAlocada.getQuantidadeSolicitada();

                                    pecaEstoqueRepository.baixarEstoque(
                                            pecaAlocada.getPecaId(),
                                            quantidade
                                    );

                                    var pecaAtiva = pecaRepository.findActiveById(pecaAlocada.getPecaId());

                                    if (pecaAtiva.getValor() == null) {
                                        return BigDecimal.ZERO;
                                    }

                                    return pecaAtiva.getValor()
                                            .multiply(BigDecimal.valueOf(quantidade));
                                })
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}