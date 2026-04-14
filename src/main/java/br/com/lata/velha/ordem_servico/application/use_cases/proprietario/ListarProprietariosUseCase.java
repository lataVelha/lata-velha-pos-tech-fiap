package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.application.assemblers.PaginatedAssembler;
import br.com.lata.velha.ordem_servico.application.assemblers.ProprietarioAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListarProprietariosUseCase {

    private final ProprietarioRepository repository;
    private final ProprietarioAssembler proprietarioAssembler;
    private final PaginatedAssembler paginatedAssembler;

    public PaginatedResult<ProprietarioResponse> execute(int page, int size) {
        return paginatedAssembler.toResponse(
                repository.findAllActivePaginated(page, size),
                proprietarioAssembler::toResponse
        );
    }
}