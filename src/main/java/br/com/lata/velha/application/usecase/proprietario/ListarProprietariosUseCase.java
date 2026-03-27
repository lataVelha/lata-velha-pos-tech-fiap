package br.com.lata.velha.application.usecase.proprietario;

import br.com.lata.velha.application.assembler.PaginatedAssembler;
import br.com.lata.velha.application.assembler.ProprietarioAssembler;
import br.com.lata.velha.application.dto.response.PaginatedResponse;
import br.com.lata.velha.application.dto.response.ProprietarioResponse;
import br.com.lata.velha.domain.repository.ProprietarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListarProprietariosUseCase {

    private final ProprietarioRepository repository;
    private final ProprietarioAssembler proprietarioAssembler;
    private final PaginatedAssembler paginatedAssembler;

    public PaginatedResponse<ProprietarioResponse> execute(int page, int size) {
        return paginatedAssembler.toResponse(
                repository.findAllPaginated(page, size),
                proprietarioAssembler::toResponse
        );
    }
}