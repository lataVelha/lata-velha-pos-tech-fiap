package br.com.lata.velha.application.usecase.proprietario;

import br.com.lata.velha.application.assembler.ProprietarioAssembler;
import br.com.lata.velha.application.dto.response.ProprietarioResponse;
import br.com.lata.velha.domain.exception.ProprietarioNotFoundException;
import br.com.lata.velha.domain.repository.ProprietarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuscarProprietarioPorDocumentoUseCase {

    private final ProprietarioRepository repository;
    private final ProprietarioAssembler assembler;

    public ProprietarioResponse execute(String documento) {
        String limpo = documento.replaceAll("[^\\dA-Za-z]", "").toUpperCase();
        return assembler.toResponse(repository.buscarPorDocumento(limpo)
                .orElseThrow(() -> new ProprietarioNotFoundException(documento)));
    }
}