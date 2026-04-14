package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.application.assemblers.ProprietarioAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.request.ProprietarioRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
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