package br.com.lata.velha.ordem_servico.application.use_cases.pecaalocada;

import br.com.lata.velha.ordem_servico.application.dtos.request.AlocarPecaRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaAlocadaResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaAlocadaRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.ExecucaoServicoRepository;
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
    private ExecucaoServicoRepository execucaoServicoRepository;

    @InjectMocks
    private AlocarPecaUseCase alocarPecaUseCase;

    @Test
    void deveAlocarPecaComSucesso() {
        // Arrange
        AlocarPecaRequest request = new AlocarPecaRequest(1L, 2L, 3);
        
        Servico servicoBase = new Servico();
        servicoBase.setId(100L);
        ExecucaoServico execucaoServico = new ExecucaoServico( servicoBase, new BigDecimal("100.0"));
        when(execucaoServicoRepository.findById(1L)).thenReturn(execucaoServico);

        Peca peca = new Peca(2L, "Pastilha", "Desc", new BigDecimal("50.0"));
        when(pecaRepository.findActiveById(2L)).thenReturn(peca);

        PecaEstoque estoque = new PecaEstoque(2L, 10);
        when(pecaEstoqueRepository.findByPecaId(2L)).thenReturn(estoque);
        when(pecaEstoqueRepository.save(any(PecaEstoque.class))).thenAnswer(i -> i.getArgument(0));

        PecaAlocada pecaAlocadaSalva = new PecaAlocada(10L, 2L, 1L, 3);
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
        when(execucaoServicoRepository.findById(1L)).thenReturn(null);

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
        
        ExecucaoServico execucaoServico = new ExecucaoServico();
        when(execucaoServicoRepository.findById(1L)).thenReturn(execucaoServico);
        when(pecaRepository.findActiveById(2L)).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> alocarPecaUseCase.execute(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Peça não encontrada");

        verify(pecaEstoqueRepository, never()).save(any());
        verify(pecaAlocadaRepository, never()).save(any());
    }
}