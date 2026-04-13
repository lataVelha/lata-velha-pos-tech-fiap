package br.com.lata.velha.application.usecase.servico;

import br.com.lata.velha.application.assembler.ServicoAssembler;
import br.com.lata.velha.application.dto.request.AtualizarServicoRequest;
import br.com.lata.velha.application.dto.response.ServicoResponse;
import br.com.lata.velha.domain.entities.Servico;
import br.com.lata.velha.domain.repository.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AtualizarServicoUseCase {

    private final ServicoRepository repository;
    private final ServicoAssembler assembler;

    public ServicoResponse execute(Long id, AtualizarServicoRequest request) {
        Servico servico = repository.findActiveById(id);

        servico.atualizar(request.nome(), request.descricao());

        Servico saved = repository.save(servico);
        return assembler.toResponse(saved);
    }
}
