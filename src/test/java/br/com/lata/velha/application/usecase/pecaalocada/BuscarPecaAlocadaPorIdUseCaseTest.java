package br.com.lata.velha.application.usecase.pecaalocada;

import br.com.lata.velha.application.dto.response.PecaAlocadaResponse;
import br.com.lata.velha.domain.model.Peca;
import br.com.lata.velha.domain.model.PecaAlocada;
import br.com.lata.velha.domain.repository.PecaAlocadaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

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
        Peca peca = new Peca(2L, "Pastilha", "Desc", new BigDecimal("50.0"));
        PecaAlocada pecaAlocada = new PecaAlocada(1L, 2L, 99L, 2);
        
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