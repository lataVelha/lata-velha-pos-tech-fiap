package br.com.lata.velha.ordem_servico.application.use_cases.pecaalocada;

import br.com.lata.velha.ordem_servico.application.dtos.response.PecaAlocadaResponse;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaAlocadaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BuscarPecaAlocadaPorIdUseCaseTest {

    @Mock
    private PecaAlocadaRepository pecaAlocadaRepository;

    @InjectMocks
    private BuscarPecaAlocadaPorIdUseCase buscarPecaAlocadaPorIdUseCase;

    @Test
    void deveBuscarPecaAlocadaComSucesso() {
        // Arrange
        PecaAlocada pecaAlocada = new PecaAlocada(2L, 99L, 2);
        pecaAlocada.setId(1L);
        
        when(pecaAlocadaRepository.findById(1L)).thenReturn(pecaAlocada);

        // Act
        PecaAlocadaResponse response = buscarPecaAlocadaPorIdUseCase.execute(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.pecaNome()).isNull();
        verify(pecaAlocadaRepository).findById(1L);
    }
}