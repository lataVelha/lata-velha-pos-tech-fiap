package br.com.lata.velha.application.usecase.ordemservico;

import br.com.lata.velha.application.assembler.OrdemServicoAssembler;
import br.com.lata.velha.application.dto.response.OrdemServicoResponse;
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

        os.getServicos().forEach(sOs -> {

            sOs.recusado(funcionario.getId());

        });

        os.reprovar(funcionario.getId());
        return ordemServicoAssembler.toResponse(ordemServicoRepository.save(os), null,null);
    }
}
