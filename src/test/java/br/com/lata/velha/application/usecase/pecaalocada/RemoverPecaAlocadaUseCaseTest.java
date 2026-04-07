package br.com.lata.velha.application.usecase.pecaalocada;

import br.com.lata.velha.domain.repository.PecaAlocadaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RemoverPecaAlocadaUseCaseTest {

    @Mock
    private PecaAlocadaRepository pecaAlocadaRepository;

    @InjectMocks
    private RemoverPecaAlocadaUseCase removerPecaAlocadaUseCase;

    @Test
    void deveRemoverPecaComSucesso() {
        // Act
        removerPecaAlocadaUseCase.execute(1L);

        // Assert
        verify(pecaAlocadaRepository).delete(1L);
    }
}