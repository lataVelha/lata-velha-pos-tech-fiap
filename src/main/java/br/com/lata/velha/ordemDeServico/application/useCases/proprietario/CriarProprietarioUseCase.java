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