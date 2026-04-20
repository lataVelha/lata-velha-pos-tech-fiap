package br.com.lata.velha.ordem_servico.application.assemblers;

import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PaginatedAssembler {

    public <T, R> PaginatedResult<R> toResponse(PaginatedResult<T> result, Function<T, R> mapper) {
        return PaginatedResult.map(result, mapper);
    }
}
