package br.com.lata.velha.application.usecase.servico;

import br.com.lata.velha.application.assembler.ServicoAssembler;
import br.com.lata.velha.application.dto.request.CadastrarServicoRequest;
import br.com.lata.velha.application.dto.response.ServicoResponse;
import br.com.lata.velha.domain.entities.Servico;
import br.com.lata.velha.domain.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CadastrarServicoUseCase {

    private final ServicoRepository repository;
    private final ServicoAssembler assembler;

    public ServicoResponse execute(CadastrarServicoRequest request) {
        Servico servico = assembler.toDomain(request);
        Servico saved = repository.save(servico);
        return assembler.toResponse(saved);
    }
}
