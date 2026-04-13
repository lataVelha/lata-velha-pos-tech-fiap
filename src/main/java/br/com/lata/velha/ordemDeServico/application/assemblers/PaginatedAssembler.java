package br.com.lata.velha.ordemDeServico.application.assemblers;

import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class PaginatedAssembler {

    public <T, R> PaginatedResult<R> toResponse(PaginatedResult<T> result, Function<T, R> mapper) {
        List<R> content = result.content()
                .stream()
                .map(mapper)
                .toList();

        return new PaginatedResult<>(
                content,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}