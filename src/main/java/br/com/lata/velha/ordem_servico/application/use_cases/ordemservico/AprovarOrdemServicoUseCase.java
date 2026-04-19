package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.request.AprovarOrdemServicoRequest;
import br.com.lata.velha.ordem_servico.application.dtos.request.AprovarServicoOsRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.AprovarOrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
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
    private final PecaEstoqueRepository pecaEstoqueRepository;
    private final PecaAlocadaRepository pecaAlocadaRepository;
    private final NotificarOrdemServicoUseCase notificarUseCase;

    @Transactional
    public AprovarOrdemServicoResponse execute(AprovarOrdemServicoRequest request) {
        var ordemServico = ordemServicoRepository.findById(request.idOs());
        var funcionario = funcionarioRepository.getById(request.idFunc());

        Map<Long, StatusExecucaoServico> statusPorId =
                request.idServicoOsAprovar().stream()
                        .collect(Collectors.toMap(
                                AprovarServicoOsRequest::idServicoOs,
                                AprovarServicoOsRequest::statusExecucaoServico
                        ));

        ordemServico.getExecucaoServicos().forEach(execucaoServico -> {
            StatusExecucaoServico novoStatus = statusPorId.get(execucaoServico.getId());
            if (novoStatus == null) return;

            switch (novoStatus) {
                case APROVADO -> {
                    execucaoServico.aprovar(funcionario.getId());

                    execucaoServico.getPecas().forEach(peca -> {
                        var estoque = pecaEstoqueRepository.findByPecaId(peca.getPecaId());

                        int quantidadeDisponivel = 0;
                        if (estoque != null && estoque.getQuantidadeArmazenada() != null) {
                            Integer jaReservado = pecaAlocadaRepository.somarQuantidadeReservadaPorPeca(peca.getPecaId());
                            quantidadeDisponivel = estoque.getQuantidadeArmazenada() - jaReservado;
                            if (quantidadeDisponivel < 0) quantidadeDisponivel = 0;
                        }

                        peca.reservar(quantidadeDisponivel);

                        if (peca.getQuantidadeEncomendada() != null && peca.getQuantidadeEncomendada() > 0) {
                            gerarEncomendaPeca(ordemServico.getId(), execucaoServico.getId(), peca.getPecaId(), peca.getQuantidadeEncomendada());
                        }
                    });
                }
                case RECUSADO -> execucaoServico.recusar(funcionario.getId());
            }
        });

        ordemServico.aprovar(funcionario.getId());
        notificarUseCase.execute(ordemServico);

        return AprovarOrdemServicoResponse.from(ordemServicoRepository.save(ordemServico));
    }

    private void gerarEncomendaPeca(Long osId, Long servicoId, Long pecaId, Integer quantidadeFaltante) {
        System.out.println(
                "Encomendar peça -> OS: " + osId +
                        " | Serviço: " + servicoId +
                        " | Peça: " + pecaId +
                        " | Quantidade: " + quantidadeFaltante
        );
    }
}
