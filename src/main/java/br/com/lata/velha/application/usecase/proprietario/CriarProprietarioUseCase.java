package br.com.lata.velha.application.usecase.proprietario;

import br.com.lata.velha.application.assembler.ProprietarioAssembler;
import br.com.lata.velha.application.dto.request.ProprietarioRequest;
import br.com.lata.velha.application.dto.response.ProprietarioResponse;
import br.com.lata.velha.domain.model.Proprietario;
import br.com.lata.velha.domain.repository.ProprietarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CriarProprietarioUseCase {

    private final ProprietarioRepository repository;
    private final ProprietarioAssembler assembler;
    private final NotificarCadastroProprietarioUseCase notificarUseCase;

    public ProprietarioResponse execute(ProprietarioRequest request) {
        Proprietario saved = repository.save(assembler.toDomain(request));
        notificarUseCase.execute(saved);
        return assembler.toResponse(saved);
    }
}