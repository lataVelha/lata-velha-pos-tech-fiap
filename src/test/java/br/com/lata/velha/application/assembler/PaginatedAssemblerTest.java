package br.com.lata.velha.application.assembler;

import br.com.lata.velha.domain.common.PaginatedResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaginatedAssemblerTest {

    private PaginatedAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new PaginatedAssembler();
    }

    @Test
    @DisplayName("deve converter PaginatedResult para PaginatedResponse aplicando mapper")
    void shouldConvertPaginatedResultToResponse() {
        PaginatedResult<String> result = new PaginatedResult<>(
                List.of("a", "b", "c"), 0, 10, 3L, 1);

        PaginatedResult<String> response = assembler.toResponse(result, String::toUpperCase);

        assertThat(response.content()).containsExactly("A", "B", "C");
        assertThat(response.page()).isZero();
        assertThat(response.size()).isEqualTo(10);
        assertThat(response.totalElements()).isEqualTo(3L);
        assertThat(response.totalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("deve retornar conteúdo vazio quando PaginatedResult não tem elementos")
    void shouldReturnEmptyContentWhenResultIsEmpty() {
        PaginatedResult<String> result = new PaginatedResult<>(List.of(), 0, 10, 0L, 0);

        PaginatedResult<String> response = assembler.toResponse(result, String::toUpperCase);

        assertThat(response.content()).isEmpty();
        assertThat(response.totalElements()).isZero();
        assertThat(response.totalPages()).isZero();
    }

    @Test
    @DisplayName("deve preservar metadados de paginação corretamente")
    void shouldPreservePaginationMetadata() {
        PaginatedResult<Integer> result = new PaginatedResult<>(
                List.of(1, 2, 3), 2, 5, 13L, 3);

        PaginatedResult<String> response = assembler.toResponse(result, Object::toString);

        assertThat(response.page()).isEqualTo(2);
        assertThat(response.size()).isEqualTo(5);
        assertThat(response.totalElements()).isEqualTo(13L);
        assertThat(response.totalPages()).isEqualTo(3);
    }

    @Test
    @DisplayName("deve aplicar função de mapeamento em todos os elementos")
    void shouldApplyMapperToAllElements() {
        PaginatedResult<Integer> result = new PaginatedResult<>(
                List.of(1, 2, 3), 0, 10, 3L, 1);

        PaginatedResult<Integer> response = assembler.toResponse(result, n -> n * 2);

        assertThat(response.content()).containsExactly(2, 4, 6);
    }
}
