package br.com.lata.velha.application.usecase.peca;

import br.com.lata.velha.application.assembler.PecaAssembler;
import br.com.lata.velha.application.dto.response.PecaResponse;
import br.com.lata.velha.domain.repository.PecaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuscarPecaPorIdUseCase {

    private final PecaRepository repository;
    private final PecaAssembler assembler;

    public PecaResponse execute(Long id) {
        return assembler.toResponse(repository.findActiveById(id));
    }
}
