package br.com.lata.velha.application.usecase.pecaalocada;

import br.com.lata.velha.application.dto.request.AtualizarPecaAlocadaRequest;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarPecaAlocadaUseCaseTest {

    @Mock
    private PecaAlocadaRepository pecaAlocadaRepository;

    @InjectMocks
    private AtualizarPecaAlocadaUseCase atualizarPecaAlocadaUseCase;

    @Test
    void deveAtualizarPecaComSucesso() {
        // Arrange
        AtualizarPecaAlocadaRequest request = new AtualizarPecaAlocadaRequest(5);
        Peca peca = new Peca(2L, "Pastilha", "Desc", new BigDecimal("50.0"));
        PecaAlocada pecaAlocada = new PecaAlocada(1L, 2L, 99L, 2);
        
        when(pecaAlocadaRepository.findById(1L)).thenReturn(pecaAlocada);
        when(pecaAlocadaRepository.save(pecaAlocada)).thenReturn(pecaAlocada);

        // Act
        PecaAlocadaResponse response = atualizarPecaAlocadaUseCase.execute(1L, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.quantidadeAlocada()).isEqualTo(5);
        verify(pecaAlocadaRepository).save(pecaAlocada);
    }

    @Test
    void deveLancarExcecaoSePecaNaoEncontrada() {
        // Arrange
        AtualizarPecaAlocadaRequest request = new AtualizarPecaAlocadaRequest(5);
        when(pecaAlocadaRepository.findById(1L)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> atualizarPecaAlocadaUseCase.execute(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Peça alocada não encontrada");

        verify(pecaAlocadaRepository, never()).save(any());
    }
}