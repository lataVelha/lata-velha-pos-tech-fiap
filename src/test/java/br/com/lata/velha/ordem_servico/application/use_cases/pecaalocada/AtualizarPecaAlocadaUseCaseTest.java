package br.com.lata.velha.ordem_servico.application.use_cases.pecaalocada;

import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarPecaAlocadaRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaAlocadaResponse;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaAlocadaRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarPecaAlocadaUseCaseTest {

    @Mock
    private PecaAlocadaRepository pecaAlocadaRepository;

    @Mock
    private PecaEstoqueRepository pecaEstoqueRepository;

    @InjectMocks
    private AtualizarPecaAlocadaUseCase atualizarPecaAlocadaUseCase;

    @Test
    void deveAtualizarPecaComSucesso() {
        // Arrange
        AtualizarPecaAlocadaRequest request = new AtualizarPecaAlocadaRequest(5);
        PecaAlocada pecaAlocada = new PecaAlocada(1L, 2L, 99L, 2);
        PecaEstoque estoque = new PecaEstoque(2L, 10);
        
        when(pecaAlocadaRepository.findById(1L)).thenReturn(pecaAlocada);
        when(pecaEstoqueRepository.findByPecaId(2L)).thenReturn(estoque);
        when(pecaEstoqueRepository.save(any(PecaEstoque.class))).thenAnswer(i -> i.getArgument(0));
        when(pecaAlocadaRepository.save(pecaAlocada)).thenReturn(pecaAlocada);

        // Act
        PecaAlocadaResponse response = atualizarPecaAlocadaUseCase.execute(1L, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.quantidadeAlocada()).isEqualTo(5);
        verify(pecaEstoqueRepository).save(any(PecaEstoque.class));
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

        verify(pecaEstoqueRepository, never()).save(any());
        verify(pecaAlocadaRepository, never()).save(any());
    }
}