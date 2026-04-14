package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;


import br.com.lata.velha.ordem_servico.application.assemblers.OrdemServicoAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.request.AprovarOrdemSevicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.AprovarServicoOsRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.AprovarOrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.enums.StatusServico;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;

import br.com.lata.velha.ordem_servico.domain.repositories.PecaAlocadaRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AprovarOrdemServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final OrdemServicoAssembler ordemServicoAssembler;
    private final PecaEstoqueRepository pecaEstoqueRepository;
    private final PecaAlocadaRepository pecaAlocadaRepository;

    @Transactional
    public AprovarOrdemServicoResponse execute(AprovarOrdemSevicoRequest request) {

        var os = ordemServicoRepository.findById(request.idOs());

        var funcionario = funcionarioRepository.getById(request.idFunc());


        Map<Long, StatusServico> statusPorId =
                request.idServicoOsAprovar().stream()
                        .collect(Collectors.toMap(
                                AprovarServicoOsRequest::idServicoOs,
                                AprovarServicoOsRequest::statusServico
                        ));

        os.getServicos().forEach(sOs -> {

            StatusServico novoStatus = statusPorId.get(sOs.getId());
            if (novoStatus == null) return;

            switch (novoStatus) {

                case APROVADO -> {

                    sOs.aprovado(funcionario.getId());

                    sOs.getPecas().forEach(peca -> {

                        var estoque =
                                pecaEstoqueRepository
                                        .findByPecaId(peca.getPecaId());

                        int quantidadeDisponivel = 0;

                        if (estoque != null &&
                                estoque.getQuantidadeArmazenada() != null) {

                            Integer jaReservado =
                                    pecaAlocadaRepository
                                            .somarQuantidadeReservadaPorPeca(
                                                    peca.getPecaId());

                            quantidadeDisponivel =
                                    estoque.getQuantidadeArmazenada() - jaReservado;

                            if (quantidadeDisponivel < 0) {
                                quantidadeDisponivel = 0;
                            }
                        }

                        peca.reservar(quantidadeDisponivel);

                        if (peca.getQuantidadeEncomendada() != null &&
                                peca.getQuantidadeEncomendada() > 0) {

                            gerarEncomendaPeca(
                                    os.getId(),
                                    sOs.getId(),
                                    peca.getPecaId(),
                                    peca.getQuantidadeEncomendada()
                            );
                        }
                    });
                }

                case RECUSADO -> {
                    sOs.recusado(funcionario.getId());
                }
            }
        });

        os.aprovar(funcionario.getId());

        return ordemServicoAssembler
                .toAprovarResponse(ordemServicoRepository.save(os));
    }

    private void gerarEncomendaPeca(Long osId,
                                    Long servicoId,
                                    Long pecaId,
                                    Integer quantidadeFaltante) {


        System.out.println(
                "Encomendar peça -> OS: " + osId +
                        " | Serviço: " + servicoId +
                        " | Peça: " + pecaId +
                        " | Quantidade: " + quantidadeFaltante
        );


    }
}