package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.assemblers.OrdemServicoAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.application.use_cases.proprietario.BuscarProprietarioPorIdUseCase;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusServico;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.ServicoOS;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReprovarOrdemServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final OrdemServicoAssembler ordemServicoAssembler;
    private final BuscarProprietarioPorIdUseCase buscarProprietarioPorIdUseCase;
    private final NotificarOrdemServicoUseCase notificarUseCase;

    public OrdemServicoResponse execute(Long osId, Long idFunc) {
        var os = ordemServicoRepository.findById(osId);
        var funcionario = funcionarioRepository.getById(idFunc);

        StatusOrdemServico status = os.getStatus();
        vailidarStatusOrdem(status, os);

        os.getServicos().forEach(sOs -> {

            StatusServico statusServico = sOs.getStatus();
            vailidarStatusServico(statusServico, sOs);

            sOs.recusado(funcionario.getId());

        });

        os.reprovar(funcionario.getId());
        notificarUseCase.execute(os);
        return ordemServicoAssembler.toResponse(ordemServicoRepository.save(os), null, null,null,null,null);
    }

    private void vailidarStatusOrdem(StatusOrdemServico status, OrdemServico os) {

        if (status == null) {
            throw new IllegalStateException("Ordem de Serviço sem status: " + os.getId());
        }
        switch (status) {
            case FINALIZADA -> throw new ResourceAlreadyExistsException(
                    "Esta Ordem de Serviço foi finalizada: " + os.getId());

            case EM_EXECUCAO -> throw new ResourceAlreadyExistsException(
                    "Esta Ordem de Serviço está em execução: " + os.getId());

            case ENTREGUE -> throw new ResourceAlreadyExistsException(
                    "Esta Ordem de Serviço já foi entregue: " + os.getId());

        }
    }

    private void vailidarStatusServico(StatusServico statusServico, ServicoOS sOs) {
        if (statusServico == StatusServico.APROVADO) {
            throw new ResourceAlreadyExistsException(
                    "Este serviço foi Aprovado " + sOs.getServico().getNome());
        }

        if (statusServico == StatusServico.FINALIZADO) {
            throw new ResourceAlreadyExistsException(
                    "Este serviço ja foi realizado " + sOs.getServico().getNome());
        }
    }

}
