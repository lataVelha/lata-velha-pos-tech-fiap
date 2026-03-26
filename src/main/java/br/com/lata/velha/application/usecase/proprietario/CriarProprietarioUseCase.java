package br.com.lata.velha.application.usecase.proprietario;

import br.com.lata.velha.application.assembler.ProprietarioAssembler;
import br.com.lata.velha.application.dto.request.ProprietarioRequest;
import br.com.lata.velha.application.dto.response.ProprietarioResponse;
import br.com.lata.velha.domain.exception.ResourceAlreadyExistsException;
import br.com.lata.velha.domain.model.Proprietario;
import br.com.lata.velha.domain.repository.ProprietarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CriarProprietarioUseCase {

    private final ProprietarioRepository repository;
    private final ProprietarioAssembler assembler;

    public ProprietarioResponse execute(ProprietarioRequest request) {
        String docLimpo = request.documento().replaceAll("[^\\d]", "");
        if (repository.existePorDocumento(docLimpo)) {
            throw new ResourceAlreadyExistsException(
                    "Já existe um proprietário com este documento: " + request.documento());
        }
        Proprietario salvo = repository.salvar(assembler.toDomain(request));
        return assembler.toResponse(salvo);
    }
}