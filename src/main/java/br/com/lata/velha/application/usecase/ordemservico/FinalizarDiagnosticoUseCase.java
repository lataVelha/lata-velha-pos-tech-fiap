package br.com.lata.velha.application.usecase.ordemservico;

import br.com.lata.velha.application.assembler.OrdemServicoAssembler;
import br.com.lata.velha.application.dto.response.OrdemServicoResponse;
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

    public OrdemServicoResponse execute(Long idOs, Long idMecanico){

        var os = ordemServicoRepository.findById(idOs);
        var mecanico = funcionarioRepository.findById(idMecanico);

        os.fimDignostico(mecanico.getId());

        return ordemServicoAssembler.toResponse(ordemServicoRepository.save(os), null,null);
    }
}
