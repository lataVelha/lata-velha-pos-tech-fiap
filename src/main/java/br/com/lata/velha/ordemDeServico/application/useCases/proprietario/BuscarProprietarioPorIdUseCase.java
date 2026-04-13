package br.com.lata.velha.ordemDeServico.application.useCases.proprietario;

import br.com.lata.velha.ordemDeServico.application.assemblers.ProprietarioAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.ordemDeServico.domain.repositories.ProprietarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuscarProprietarioPorIdUseCase {

    private final ProprietarioRepository repository;
    private final ProprietarioAssembler assembler;

    public ProprietarioResponse execute(Long id) {
        return assembler.toResponse(repository.findActiveById(id));
    }
}