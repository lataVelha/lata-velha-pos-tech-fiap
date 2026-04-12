package br.com.lata.velha.application.usecase.ordemservico;

import br.com.lata.velha.application.assembler.OrdemServicoAssembler;
import br.com.lata.velha.application.dto.response.OrdemServicoResponse;
import br.com.lata.velha.application.usecase.proprietario.BuscarProprietarioPorIdUseCase;
import br.com.lata.velha.domain.enuns.StatusOrdemServico;
import br.com.lata.velha.domain.enuns.StatusServico;
import br.com.lata.velha.domain.exception.ResourceAlreadyExistsException;
import br.com.lata.velha.domain.model.OrdemServico;
import br.com.lata.velha.domain.model.ServicoOS;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import br.com.lata.velha.domain.repository.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReprovarOrdemServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final OrdemServicoAssembler ordemServicoAssembler;
    private final BuscarProprietarioPorIdUseCase buscarProprietarioPorIdUseCase;

    public OrdemServicoResponse execute(Long osId, Long idFunc) {

        var os = ordemServicoRepository.findById(osId);
        var funcionario = funcionarioRepository.findById(idFunc);

        StatusOrdemServico status = os.getStatus();
        vailidarStatusOrdem(status, os);

        os.getServicos().forEach(sOs -> {

            StatusServico statusServico = sOs.getStatus();
            vailidarStatusServico(statusServico, sOs);

            sOs.recusado(funcionario.getId());

        });

        os.reprovar(funcionario.getId());
        enviarNotificao(os);
        return ordemServicoAssembler.toResponse(ordemServicoRepository.save(os), null, null);
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

    private void enviarNotificao(OrdemServico ordemServico){

        var proprietario = buscarProprietarioPorIdUseCase.execute(ordemServico.getProprietarioId());

        // chamar o email
    }
}
