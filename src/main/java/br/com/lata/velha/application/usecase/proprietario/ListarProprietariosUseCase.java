package br.com.lata.velha.application.usecase.proprietario;

import br.com.lata.velha.application.assembler.ProprietarioAssembler;
import br.com.lata.velha.application.dto.response.PaginatedResponse;
import br.com.lata.velha.application.dto.response.ProprietarioResponse;
import br.com.lata.velha.domain.model.Proprietario;
import br.com.lata.velha.domain.common.PaginatedResult;
import br.com.lata.velha.domain.repository.ProprietarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarProprietariosUseCase {

    private final ProprietarioRepository repository;
    private final ProprietarioAssembler assembler;

    public ListarProprietariosUseCase(ProprietarioRepository repository,
                                      ProprietarioAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    public PaginatedResponse<ProprietarioResponse> execute(int page, int size) {
        PaginatedResult<Proprietario> resultado = repository.listarPaginado(page, size);

        List<ProprietarioResponse> content = resultado.content()
                .stream()
                .map(assembler::toResponse)
                .toList();

        return new PaginatedResponse<>(
                content,
                resultado.page(),
                resultado.size(),
                resultado.totalElements(),
                resultado.totalPages()
        );
    }
}