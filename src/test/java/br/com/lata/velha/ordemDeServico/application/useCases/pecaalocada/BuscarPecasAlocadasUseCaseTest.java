package br.com.lata.velha.ordemDeServico.application.useCases.pecaalocada;

import br.com.lata.velha.ordemDeServico.application.dtos.response.PecaAlocadaResponse;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordemDeServico.domain.entities.Peca;
import br.com.lata.velha.ordemDeServico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordemDeServico.domain.repositories.PecaAlocadaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BuscarPecasAlocadasUseCaseTest {

    @Mock
    private PecaAlocadaRepository pecaAlocadaRepository;

    @InjectMocks
    private BuscarPecasAlocadasUseCase buscarPecasAlocadasUseCase;

    @Test
    void deveBuscarPecasDeUmServicoComSucesso() {
        // Arrange
        Peca peca = new Peca(2L, "Pastilha", "Desc", new BigDecimal("50.0"));
        PecaAlocada peca1 = new PecaAlocada(1L, 2L, 99L, 2);
        PecaAlocada peca2 = new PecaAlocada(2L, 2L, 99L, 4);
        
        PaginatedResult<PecaAlocada> paginatedResult = new PaginatedResult<>(
                List.of(peca1, peca2), 0, 10, 2L, 1
        );

        when(pecaAlocadaRepository.findByServicoOsId(99L, 0, 10)).thenReturn(paginatedResult);

        // Act
        PaginatedResult<PecaAlocadaResponse> response = buscarPecasAlocadasUseCase.execute(99L, 0, 10);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(2);
        assertThat(response.content().get(0).id()).isEqualTo(1L);
        assertThat(response.content().get(1).id()).isEqualTo(2L);
        assertThat(response.content().get(0).servicoOsId()).isEqualTo(99L);
        verify(pecaAlocadaRepository).findByServicoOsId(99L, 0, 10);
    }
}