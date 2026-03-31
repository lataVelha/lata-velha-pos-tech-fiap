package br.com.lata.velha.application.usecase.funcionario;

import br.com.lata.velha.application.assembler.PaginatedAssembler;
import br.com.lata.velha.application.assembler.FuncionarioAssembler;
import br.com.lata.velha.application.dto.response.PaginatedResponse;
import br.com.lata.velha.application.dto.response.FuncionarioResponse;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuscarFuncionariosUseCase {

    private final FuncionarioRepository repository;
    private final FuncionarioAssembler assembler;
    private final PaginatedAssembler paginatedAssembler;

    public PaginatedResponse<FuncionarioResponse> execute(int page, int size) {
        return paginatedAssembler.toResponse(
                repository.findAllActivePaginated(page, size),
                assembler::toResponse
        );
    }
}