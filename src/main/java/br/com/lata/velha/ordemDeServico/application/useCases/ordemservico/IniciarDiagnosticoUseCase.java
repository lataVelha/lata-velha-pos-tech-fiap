package br.com.lata.velha.ordemDeServico.application.useCases.ordemservico;

import br.com.lata.velha.ordemDeServico.application.assemblers.OrdemServicoAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordemDeServico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordemDeServico.domain.repositories.OrdemServicoRepository;
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