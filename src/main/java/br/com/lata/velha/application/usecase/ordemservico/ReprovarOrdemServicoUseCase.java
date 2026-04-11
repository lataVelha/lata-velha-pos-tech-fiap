package br.com.lata.velha.application.usecase.ordemservico;

import br.com.lata.velha.application.assembler.OrdemServicoAssembler;
import br.com.lata.velha.application.dto.response.OrdemServicoResponse;
import br.com.lata.velha.domain.enuns.StatusOrdemServico;
import br.com.lata.velha.domain.enuns.StatusServico;
import br.com.lata.velha.domain.exception.ResourceAlreadyExistsException;
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

    public OrdemServicoResponse execute(Long osId, Long idFunc) {

        var os = ordemServicoRepository.findById(osId);
        var funcionario = funcionarioRepository.findById(idFunc);

        StatusOrdemServico status = os.getStatus();

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
        os.getServicos().forEach(sOs -> {

            StatusServico statusServico = sOs.getStatus();

            if(statusServico == StatusServico.APROVADO){
                throw new ResourceAlreadyExistsException(
                        "Este serviço foi Aprovado " + sOs.getServico().getNome());
            }

           if(statusServico == StatusServico.FINALIZADO){
               throw new ResourceAlreadyExistsException(
                       "Este serviço ja foi realizado " + sOs.getServico().getNome());
            }
            sOs.recusado(funcionario.getId());

        });

        os.reprovar(funcionario.getId());
        return ordemServicoAssembler.toResponse(ordemServicoRepository.save(os), null,null);
    }
}
