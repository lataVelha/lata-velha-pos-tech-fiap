package br.com.lata.velha.application.assembler;

import br.com.lata.velha.application.dto.response.PaginatedResponse;
import br.com.lata.velha.domain.common.PaginatedResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class PaginatedAssembler {

    public <T, R> PaginatedResponse<R> toResponse(PaginatedResult<T> result, Function<T, R> mapper) {
        List<R> content = result.content()
                .stream()
                .map(mapper)
                .toList();

        return new PaginatedResponse<>(
                content,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}