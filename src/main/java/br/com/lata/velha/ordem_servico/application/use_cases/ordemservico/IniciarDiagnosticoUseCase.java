package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.assemblers.OrdemServicoAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IniciarDiagnosticoUseCase {

    private final OrdemServicoRepository repository;
    private final FuncionarioRepository funcionarioRepository;
    private final OrdemServicoAssembler ordemServicoAssembler;
    private final NotificarOrdemServicoUseCase notificarUseCase;

    public OrdemServicoResponse execute(Long idOs, Long idMecanico) {

        var ordemServico = repository.findById(idOs);

        var mecanico = funcionarioRepository.getById(idMecanico);

        ordemServico.iniciarDiagnostico(mecanico.getId());

        var ordemServicoIniciada = repository.save(ordemServico);
        notificarUseCase.execute(ordemServicoIniciada);

        return ordemServicoAssembler.toResponse(ordemServicoIniciada,null, null,null,null,null);
    }
}