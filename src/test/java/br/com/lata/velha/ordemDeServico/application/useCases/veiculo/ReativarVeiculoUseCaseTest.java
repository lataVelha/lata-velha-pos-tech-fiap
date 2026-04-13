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
class ReativarVeiculoUseCaseTest {

    @Mock
    private VeiculoRepository repository;

    @Mock
    private VeiculoAssembler assembler;

    @InjectMocks
    private ReativarVeiculoUseCase useCase;

    @Test
    @DisplayName("deve reativar veículo inativo")
    void shouldReactivateVeiculo() {
        Veiculo veiculo = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata");
        veiculo.deactivate();
        VeiculoResponse response = mock(VeiculoResponse.class);

        when(repository.findInactiveById(1L)).thenReturn(veiculo);
        when(repository.save(veiculo)).thenReturn(veiculo);
        when(assembler.toResponse(veiculo)).thenReturn(response);

        VeiculoResponse result = useCase.execute(1L);

        assertTrue(veiculo.isAtivo());
        assertNotNull(result);
        verify(repository).findInactiveById(1L);
        verify(repository).save(veiculo);
    }
}