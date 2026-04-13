package br.com.lata.velha.ordemDeServico.application.useCases.ordemservico;

import br.com.lata.velha.ordemDeServico.application.assemblers.OrdemServicoAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.request.AprovarOrdemSevicoRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.request.AprovarServicoOsRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.response.AprovarOrdemServicoResponse;
import br.com.lata.velha.ordemDeServico.domain.enums.StatusServico;
import br.com.lata.velha.ordemDeServico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordemDeServico.domain.repositories.OrdemServicoRepository;
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
                }
                case RECUSADO -> {
                    sOs.recusado(funcionario.getId());
                }
            }
        });

        os.aprovar(funcionario.getId());

        return ordemServicoAssembler.toAprovarResponse(ordemServicoRepository.save(os));
    }
}
