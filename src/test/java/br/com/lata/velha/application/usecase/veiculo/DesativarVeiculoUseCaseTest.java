package br.com.lata.velha.application.usecase.veiculo;

import br.com.lata.velha.domain.entities.Veiculo;
import br.com.lata.velha.domain.repository.VeiculoRepository;
import br.com.lata.velha.domain.valueObject.Placa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DesativarVeiculoUseCaseTest {

    @Mock
    private VeiculoRepository repository;

    @InjectMocks
    private DesativarVeiculoUseCase useCase;

    @Test
    @DisplayName("deve desativar veículo (soft delete)")
    void shouldDeactivateVeiculo() {
        Veiculo veiculo = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata");

        when(repository.findActiveById(1L)).thenReturn(veiculo);
        when(repository.save(veiculo)).thenReturn(veiculo);

        useCase.execute(1L);

        assertFalse(veiculo.isAtivo());
        verify(repository).findActiveById(1L);
        verify(repository).save(veiculo);
    }
}