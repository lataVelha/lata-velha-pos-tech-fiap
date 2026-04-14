package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.application.assemblers.ServicoAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.ordem_servico.domain.repositories.ServicoRepository;
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
