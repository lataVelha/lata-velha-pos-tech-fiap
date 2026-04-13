package br.com.lata.velha.application.usecase.peca;

import br.com.lata.velha.application.assembler.PaginatedAssembler;
import br.com.lata.velha.application.assembler.PecaAssembler;
import br.com.lata.velha.application.dto.response.PecaResponse;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.domain.entities.Peca;
import br.com.lata.velha.domain.repository.PecaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarPecasUseCaseTest {

    @Mock
    private PecaRepository repository;

    @Mock
    private PecaAssembler assembler;

    @Test
    @DisplayName("Deve listar peças ativas de forma paginada")
    void deveListarPecasAtivasDeFormaPaginada() {
        var useCase = new BuscarPecasUseCase(repository, assembler, new PaginatedAssembler());

        var peca1 = new Peca(1L, "Filtro", "Filtro de óleo", new BigDecimal("30.00"), true);
        var peca2 = new Peca(2L, "Óleo", "Óleo sintético", new BigDecimal("59.90"), true);

        var page = new PaginatedResult<>(List.of(peca1, peca2), 0, 10, 2, 1);

        when(repository.findAllActivePaginated(0, 10)).thenReturn(page);
        when(assembler.toResponse(peca1)).thenReturn(
            new PecaResponse(1L, "Filtro", "Filtro de óleo", new BigDecimal("30.00"), true)
        );
        when(assembler.toResponse(peca2)).thenReturn(
            new PecaResponse(2L, "Óleo", "Óleo sintético", new BigDecimal("59.90"), true)
        );

        PaginatedResult<PecaResponse> result = useCase.execute(0, 10);

        assertEquals(2, result.content().size());
        assertEquals(0, result.page());
        assertEquals(10, result.size());
        assertEquals(2, result.totalElements());
        verify(repository).findAllActivePaginated(0, 10);
    }
}
