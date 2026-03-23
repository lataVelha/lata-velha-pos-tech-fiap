package br.com.lata.velha.domain.common;

import java.util.List;

public record PaginatedResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}