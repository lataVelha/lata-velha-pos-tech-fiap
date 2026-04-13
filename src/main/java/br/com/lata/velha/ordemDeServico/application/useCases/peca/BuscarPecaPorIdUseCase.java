package br.com.lata.velha.ordemDeServico.application.useCases.peca;

import br.com.lata.velha.ordemDeServico.application.assemblers.PecaAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.response.PecaResponse;
import br.com.lata.velha.ordemDeServico.domain.repositories.PecaRepository;
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
