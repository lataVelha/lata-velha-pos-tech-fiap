package br.com.lata.velha.application.usecase.proprietario;

import br.com.lata.velha.application.assembler.ProprietarioAssembler;
import br.com.lata.velha.application.dto.response.ProprietarioResponse;
import br.com.lata.velha.domain.exception.ProprietarioNotFoundException;
import br.com.lata.velha.domain.repository.ProprietarioRepository;
import org.springframework.stereotype.Service;

@Service
public class BuscarProprietarioUseCase {

    private final ProprietarioRepository repository;
    private final ProprietarioAssembler assembler;

    public BuscarProprietarioUseCase(ProprietarioRepository repository,
                                     ProprietarioAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    public ProprietarioResponse porId(Long id) {
        return assembler.toResponse(repository.buscarPorId(id)
                .orElseThrow(() -> new ProprietarioNotFoundException(id)));
    }

    public ProprietarioResponse porDocumento(String documento) {
        String limpo = documento.replaceAll("[^\\d]", "");
        return assembler.toResponse(repository.buscarPorDocumento(limpo)
                .orElseThrow(() -> new ProprietarioNotFoundException(documento)));
    }
}