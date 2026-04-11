package br.com.lata.velha.application.usecase.servico;

import br.com.lata.velha.application.assembler.ServicoAssembler;
import br.com.lata.velha.application.dto.response.ServicoResponse;
import br.com.lata.velha.domain.repository.ServicoRepository;
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
