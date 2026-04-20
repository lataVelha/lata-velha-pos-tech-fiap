package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import br.com.lata.velha.ordem_servico.domain.valueObjects.Placa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarVeiculosUseCaseTest {

    @Mock
    private VeiculoRepository repository;

    @InjectMocks
    private ListarVeiculosUseCase useCase;

    @Test
    @DisplayName("deve listar veículos paginado")
    void shouldListPaginated() {
        Veiculo veiculo = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata");
        PaginatedResult<Veiculo> paginatedResult = new PaginatedResult<>(
                List.of(veiculo), 0, 10, 1, 1);
        when(repository.findAllActivePaginated(0, 10)).thenReturn(paginatedResult);

        PaginatedResult<VeiculoResponse> result = useCase.execute(0, 10);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        verify(repository).findAllActivePaginated(0, 10);
    }
}