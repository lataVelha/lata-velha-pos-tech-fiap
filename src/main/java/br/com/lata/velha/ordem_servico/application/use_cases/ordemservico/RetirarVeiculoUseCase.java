package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.assemblers.OrdemServicoAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import br.com.lata.velha.ordem_servico.domain.enums.StatusServico;
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
    private final PecaAlocadaRepository pecaAlocadaRepository;
    private final PecaEstoqueRepository pecaEstoqueRepository;
    private final PecaRepository pecaRepository;

    public OrdemServicoResponse execute(Long idOs, Long idFuncionario) {

        var os = ordemServicoRepository.findById(idOs);
        var funcionario = funcionarioRepository.getById(idFuncionario);

        if (!StatusOrdemServico.FINALIZADA.equals(os.getStatus())) {
            throw new ResourceAlreadyExistsException(
                    "Esta Ordem de Serviço não foi Finalizada: " + os.getId());
        }

        BigDecimal totalServicos = os.getServicos().stream()
                .map(sos -> {

                    if (!StatusServico.FINALIZADO.equals(sos.getStatus())) {
                        throw new ResourceAlreadyExistsException(
                                "Este Serviço não foi Finalizado: " + sos.getId());
                    }

                    return sos.getValorMaoDeObra() != null ? sos.getValorMaoDeObra() : BigDecimal.ZERO;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal totalPecas = os.getServicos().stream()
                .flatMap(sos -> sos.getPecas().stream()
                        .map(p -> {

                            var pecaAlocada = pecaAlocadaRepository
                                    .findByPecaIdAndServicoOsId(p.getPecaId(), sos.getId());

                            if (!StatusPecaAlocada.INSTALADA.equals(pecaAlocada.getStatus())) {
                                throw new ResourceAlreadyExistsException("Peça não instalada!");
                            }

                            Integer quantidade = pecaAlocada.getQuantidadeSolicitada();

                            pecaEstoqueRepository.baixarEstoque(
                                    pecaAlocada.getPecaId(),
                                    quantidade
                            );

                            var peca = pecaRepository.findActiveById(pecaAlocada.getPecaId());
                            if (peca.getValor() == null) {
                                return BigDecimal.ZERO;
                            }

                            return peca.getValor()
                                    .multiply(BigDecimal.valueOf(quantidade));
                        })

                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOrdemServico = totalServicos.add(totalPecas);
        os.entregar(funcionario.getId());

        return ordemServicoAssembler.toResponse(
                ordemServicoRepository.save(os),
                null,
                null,
                totalServicos,
                totalPecas,
                totalOrdemServico
        );
    }
}

