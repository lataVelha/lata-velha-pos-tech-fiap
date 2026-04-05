package br.com.lata.velha.application.usecase.veiculo;

import br.com.lata.velha.application.assembler.PaginatedAssembler;
import br.com.lata.velha.application.assembler.VeiculoAssembler;
import br.com.lata.velha.application.dto.response.VeiculoResponse;
import br.com.lata.velha.domain.common.PaginatedResult;
import br.com.lata.velha.domain.model.Veiculo;
import br.com.lata.velha.domain.repository.VeiculoRepository;
import br.com.lata.velha.domain.valueObject.Placa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarVeiculosUseCaseTest {

    @Mock
    private VeiculoRepository repository;

    @Mock
    private VeiculoAssembler veiculoAssembler;

    @Mock
    private PaginatedAssembler paginatedAssembler;

    @InjectMocks
    private ListarVeiculosUseCase useCase;

    @Test
    @DisplayName("deve listar veículos paginado")
    void shouldListPaginated() {
        Veiculo veiculo = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata");
        PaginatedResult<Veiculo> paginatedResult = new PaginatedResult<>(
                List.of(veiculo), 0, 10, 1, 1);
        PaginatedResult<VeiculoResponse> paginatedResponse = new PaginatedResult<>(
                List.of(mock(VeiculoResponse.class)), 0, 10, 1, 1);

        when(repository.findAllActivePaginated(0, 10)).thenReturn(paginatedResult);
        when(paginatedAssembler.toResponse(eq(paginatedResult), any(Function.class)))
                .thenReturn(paginatedResponse);

        PaginatedResult<VeiculoResponse> result = useCase.execute(0, 10);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        verify(repository).findAllActivePaginated(0, 10);
    }
}