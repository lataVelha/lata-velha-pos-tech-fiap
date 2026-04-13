package br.com.lata.velha.ordemDeServico.application.useCases.servico;

import br.com.lata.velha.ordemDeServico.application.assemblers.ServicoAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.ordemDeServico.domain.repositories.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuscarServicoPorIdUseCase {

    private final ServicoRepository repository;
    private final ServicoAssembler assembler;

    public ServicoResponse execute(Long id) {
        return assembler.toResponse(repository.findActiveById(id));
    }
}
