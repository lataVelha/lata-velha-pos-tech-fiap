package br.com.lata.velha.application.usecase.ordemservico;

import br.com.lata.velha.application.assembler.OrdemServicoAssembler;
import br.com.lata.velha.application.dto.response.OrdemServicoResponse;
import br.com.lata.velha.application.usecase.proprietario.BuscarProprietarioPorIdUseCase;
import br.com.lata.velha.domain.model.OrdemServico;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import br.com.lata.velha.domain.repository.OrdemServicoRepository;
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
        var mecanico = funcionarioRepository.findById(idMecanico);

        os.finalizarDiagnostico(mecanico.getId());
        enviarNotificao(os);

        return ordemServicoAssembler.toResponse(ordemServicoRepository.save(os), null,null);
    }

    private void enviarNotificao(OrdemServico ordemServico){

        var proprietario = buscarProprietarioPorIdUseCase.execute(ordemServico.getProprietarioId());

        // chamar o email
    }
}
