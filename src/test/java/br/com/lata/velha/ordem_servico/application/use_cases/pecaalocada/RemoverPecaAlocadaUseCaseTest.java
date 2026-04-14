package br.com.lata.velha.ordem_servico.application.use_cases.pecaalocada;

import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaAlocadaRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverPecaAlocadaUseCaseTest {

    @Mock
    private PecaAlocadaRepository pecaAlocadaRepository;

    @Mock
    private PecaEstoqueRepository pecaEstoqueRepository;

    @InjectMocks
    private RemoverPecaAlocadaUseCase removerPecaAlocadaUseCase;

    @Test
    void deveRemoverPecaComSucesso() {
        PecaAlocada pecaAlocada = new PecaAlocada(1L, 2L, 10L, 3);
        PecaEstoque estoque = new PecaEstoque(2L, 7);

        when(pecaAlocadaRepository.findById(1L)).thenReturn(pecaAlocada);
        when(pecaEstoqueRepository.findByPecaId(2L)).thenReturn(estoque);
        when(pecaEstoqueRepository.save(any(PecaEstoque.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        removerPecaAlocadaUseCase.execute(1L);

        // Assert
        verify(pecaEstoqueRepository).save(any(PecaEstoque.class));
        verify(pecaAlocadaRepository).delete(1L);
    }
}