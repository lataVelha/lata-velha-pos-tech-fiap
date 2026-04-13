package br.com.lata.velha.application.usecase.ordemservico;

import br.com.lata.velha.application.assembler.OrdemServicoAssembler;
import br.com.lata.velha.application.dto.response.OrdemServicoResponse;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import br.com.lata.velha.domain.repository.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IniciarDiagnosticoUseCase {

    private final OrdemServicoRepository repository;

    private final FuncionarioRepository funcionarioRepository;

    private final OrdemServicoAssembler ordemServicoAssembler;

    public OrdemServicoResponse execute(Long idOs, Long idMecanico) {

        var os = repository.findById(idOs);

        var mecanico = funcionarioRepository.getById(idMecanico);

        os.iniciarDiagnostico(mecanico.getId());

        var osIniciada = repository.save(os);

        return ordemServicoAssembler.toResponse(osIniciada,null, null);
    }
}