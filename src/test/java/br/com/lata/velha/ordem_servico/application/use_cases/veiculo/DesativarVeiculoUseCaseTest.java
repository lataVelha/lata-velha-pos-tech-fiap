package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import br.com.lata.velha.ordem_servico.domain.value_objects.Placa;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        when(repository.getActiveById(1L)).thenReturn(veiculo);
        when(repository.save(veiculo)).thenReturn(veiculo);

        useCase.execute(1L);

        assertFalse(veiculo.isAtivo());
        verify(repository).getActiveById(1L);
        verify(repository).save(veiculo);
    }
}