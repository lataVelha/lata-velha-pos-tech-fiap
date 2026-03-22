package br.com.lata.velha.application.usecase.proprietario;

import br.com.lata.velha.application.assembler.ProprietarioAssembler;
import br.com.lata.velha.application.dto.request.ProprietarioRequest;
import br.com.lata.velha.application.dto.response.ProprietarioResponse;
import br.com.lata.velha.domain.exception.ProprietarioNotFoundException;
import br.com.lata.velha.domain.model.Proprietario;
import br.com.lata.velha.domain.repository.ProprietarioRepository;
import org.springframework.stereotype.Service;

@Service
public class AtualizarProprietarioUseCase {

    private final ProprietarioRepository repository;
    private final ProprietarioAssembler assembler;

    public AtualizarProprietarioUseCase(ProprietarioRepository repository,
                                        ProprietarioAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    public ProprietarioResponse execute(Long id, ProprietarioRequest request) {
        Proprietario existente = repository.buscarPorId(id)
                .orElseThrow(() -> new ProprietarioNotFoundException(id));
        assembler.updateDomain(existente, request);
        Proprietario salvo = repository.salvar(existente);
        return assembler.toResponse(salvo);
    }
}