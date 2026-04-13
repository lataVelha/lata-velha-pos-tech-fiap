package br.com.lata.velha.ordemDeServico.application.useCases.proprietario;

import br.com.lata.velha.ordemDeServico.application.assemblers.PaginatedAssembler;
import br.com.lata.velha.ordemDeServico.application.assemblers.ProprietarioAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordemDeServico.domain.repositories.ProprietarioRepository;
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