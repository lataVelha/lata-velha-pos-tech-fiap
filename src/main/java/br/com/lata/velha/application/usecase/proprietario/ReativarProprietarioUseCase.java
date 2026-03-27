package br.com.lata.velha.application.usecase.proprietario;

import br.com.lata.velha.application.assembler.ProprietarioAssembler;
import br.com.lata.velha.application.dto.response.ProprietarioResponse;
import br.com.lata.velha.domain.model.Proprietario;
import br.com.lata.velha.domain.repository.ProprietarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReativarProprietarioUseCase {

    private final ProprietarioRepository repository;
    private final ProprietarioAssembler assembler;

    public ProprietarioResponse execute(Long id) {
        Proprietario proprietario = repository.findInactiveById(id);
        proprietario.activate();
        Proprietario salvo = repository.save(proprietario);
        return assembler.toResponse(salvo);
    }
}