package br.com.lata.velha.application.usecase.pecaalocada;

import br.com.lata.velha.application.dto.request.AlocarPecaRequest;
import br.com.lata.velha.application.dto.response.PecaAlocadaResponse;
import br.com.lata.velha.domain.entities.Peca;
import br.com.lata.velha.domain.entities.PecaAlocada;
import br.com.lata.velha.domain.entities.PecaEstoque;
import br.com.lata.velha.domain.entities.Servico;
import br.com.lata.velha.domain.entities.ServicoOS;
import br.com.lata.velha.domain.repository.PecaAlocadaRepository;
import br.com.lata.velha.domain.repository.PecaEstoqueRepository;
import br.com.lata.velha.domain.repository.PecaRepository;
import br.com.lata.velha.domain.repository.ServicoOSRepository;
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
class AlocarPecaUseCaseTest {

    @Mock
    private PecaAlocadaRepository pecaAlocadaRepository;

    @Mock
    private PecaRepository pecaRepository;

    @Mock
    private PecaEstoqueRepository pecaEstoqueRepository;

    @Mock
    private ServicoOSRepository servicoOSRepository;

    @InjectMocks
    private AlocarPecaUseCase alocarPecaUseCase;

    @Test
    void deveAlocarPecaComSucesso() {
        // Arrange
        AlocarPecaRequest request = new AlocarPecaRequest(1L, 2L, 3);
        
        Servico servicoBase = new Servico();
        servicoBase.setId(100L);
        ServicoOS servicoOS = new ServicoOS( servicoBase, new BigDecimal("100.0"));
        when(servicoOSRepository.findById(1L)).thenReturn(servicoOS);

        Peca peca = new Peca(2L, "Pastilha", "Desc", new BigDecimal("50.0"));
        when(pecaRepository.findActiveById(2L)).thenReturn(peca);

        PecaEstoque estoque = new PecaEstoque(2L, 10);
        when(pecaEstoqueRepository.findByPecaId(2L)).thenReturn(estoque);
        when(pecaEstoqueRepository.save(any(PecaEstoque.class))).thenAnswer(i -> i.getArgument(0));

        PecaAlocada pecaAlocadaSalva = new PecaAlocada(10L, 2L,  3);
        when(pecaAlocadaRepository.save(any(PecaAlocada.class))).thenReturn(pecaAlocadaSalva);

        // Act
        PecaAlocadaResponse response = alocarPecaUseCase.execute(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.pecaId()).isEqualTo(2L);
        assertThat(response.quantidadeAlocada()).isEqualTo(3);
        assertThat(response.servicoOsId()).isEqualTo(1L);

        verify(pecaEstoqueRepository).save(any(PecaEstoque.class));
        verify(pecaAlocadaRepository).save(any(PecaAlocada.class));
    }

    @Test
    void deveLancarExcecaoQuandoServicoNaoEncontrado() {
        // Arrange
        AlocarPecaRequest request = new AlocarPecaRequest(1L, 2L, 3);
        when(servicoOSRepository.findById(1L)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> alocarPecaUseCase.execute(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Serviço OS não encontrado");
        
        verify(pecaRepository, never()).findActiveById(anyLong());
        verify(pecaEstoqueRepository, never()).save(any());
        verify(pecaAlocadaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoPecaNaoEncontrada() {
        // Arrange
        AlocarPecaRequest request = new AlocarPecaRequest(1L, 2L, 3);
        
        ServicoOS servicoOS = new ServicoOS();
        when(servicoOSRepository.findById(1L)).thenReturn(servicoOS);
        when(pecaRepository.findActiveById(2L)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> alocarPecaUseCase.execute(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Peça não encontrada");

        verify(pecaEstoqueRepository, never()).save(any());
        verify(pecaAlocadaRepository, never()).save(any());
    }
}