package br.com.lata.velha.ordemDeServico.application.useCases.veiculo;

import br.com.lata.velha.ordemDeServico.application.assemblers.VeiculoAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordemDeServico.domain.entities.Veiculo;
import br.com.lata.velha.ordemDeServico.domain.repositories.VeiculoRepository;
import br.com.lata.velha.ordemDeServico.domain.valueObjects.Placa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuscarVeiculoPorIdUseCaseTest {

    @Mock
    private VeiculoRepository repository;

    @Mock
    private VeiculoAssembler assembler;

    @InjectMocks
    private BuscarVeiculoPorIdUseCase useCase;

    @Test
    @DisplayName("deve buscar veículo por id")
    void shouldFindById() {
        Veiculo domain = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata");
        VeiculoResponse response = mock(VeiculoResponse.class);

        when(repository.findActiveById(1L)).thenReturn(domain);
        when(assembler.toResponse(domain)).thenReturn(response);

        VeiculoResponse result = useCase.execute(1L);

        assertNotNull(result);
        verify(repository).findActiveById(1L);
        verify(assembler).toResponse(domain);
    }
}