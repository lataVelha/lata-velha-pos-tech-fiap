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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarVeiculoPorIdUseCaseTest {

    @Mock
    private VeiculoRepository repository;

    @InjectMocks
    private BuscarVeiculoPorIdUseCase useCase;

    @Test
    @DisplayName("deve buscar veículo por id")
    void shouldFindById() {
        Veiculo domain = new Veiculo(1L, 1L, Placa.of("ABC1234"), "Fiat", "Uno", 2020, "Prata");
        when(repository.getActiveById(1L)).thenReturn(domain);

        var result = useCase.execute(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.placa()).isEqualTo("ABC-1234");
        verify(repository).getActiveById(1L);
    }
}
