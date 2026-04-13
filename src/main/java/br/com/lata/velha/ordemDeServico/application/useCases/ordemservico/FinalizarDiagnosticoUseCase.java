package br.com.lata.velha.ordemDeServico.application.useCases.ordemservico;

import br.com.lata.velha.ordemDeServico.application.assemblers.OrdemServicoAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordemDeServico.application.useCases.proprietario.BuscarProprietarioPorIdUseCase;
import br.com.lata.velha.ordemDeServico.domain.entities.OrdemServico;
import br.com.lata.velha.ordemDeServico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordemDeServico.domain.repositories.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FinalizarDiagnosticoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final OrdemServicoAssembler ordemServicoAssembler;
    private final BuscarProprietarioPorIdUseCase buscarProprietarioPorIdUseCase;

    public OrdemServicoResponse execute(Long idOs, Long idMecanico){

        var os = ordemServicoRepository.findById(idOs);
        var mecanico = funcionarioRepository.getById(idMecanico);

        os.finalizarDiagnostico(mecanico.getId());
        enviarNotificao(os);

        return ordemServicoAssembler.toResponse(ordemServicoRepository.save(os), null,null);
    }

    private void enviarNotificao(OrdemServico ordemServico){

        var proprietario = buscarProprietarioPorIdUseCase.execute(ordemServico.getProprietarioId());

        // chamar o email
    }
}
