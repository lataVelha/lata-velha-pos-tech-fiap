package br.com.lata.velha.ordemDeServico.application.useCases.proprietario;

import br.com.lata.velha.ordemDeServico.application.assemblers.ProprietarioAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.request.ProprietarioRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.ordemDeServico.domain.entities.Proprietario;
import br.com.lata.velha.ordemDeServico.domain.repositories.ProprietarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AtualizarProprietarioUseCase {

    private final ProprietarioRepository repository;
    private final ProprietarioAssembler assembler;

    public ProprietarioResponse execute(Long id, ProprietarioRequest request) {
        Proprietario existing = repository.findActiveById(id);
        assembler.updateDomain(existing, request);
        Proprietario saved = repository.save(existing);
        return assembler.toResponse(saved);
    }
}