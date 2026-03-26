package br.com.lata.velha.application.usecase.proprietario;

import br.com.lata.velha.application.assembler.ProprietarioAssembler;
import br.com.lata.velha.application.dto.response.ProprietarioResponse;
import br.com.lata.velha.domain.exception.ProprietarioNotFoundException;
import br.com.lata.velha.domain.repository.ProprietarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuscarProprietarioPorIdUseCase {

    private final ProprietarioRepository repository;
    private final ProprietarioAssembler assembler;

    public ProprietarioResponse execute(Long id) {
        return assembler.toResponse(repository.buscarPorId(id)
                .orElseThrow(() -> new ProprietarioNotFoundException(id)));
    }
}