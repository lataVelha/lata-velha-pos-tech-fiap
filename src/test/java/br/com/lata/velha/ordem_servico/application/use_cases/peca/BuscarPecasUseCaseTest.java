package br.com.lata.velha.ordem_servico.application.use_cases.peca;

import br.com.lata.velha.ordem_servico.application.dtos.response.PecaResponse;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarPecasUseCaseTest {

    @Mock
    private PecaRepository repository;

    @InjectMocks
    private BuscarPecasUseCase useCase;

    @Test
    @DisplayName("Deve listar peças ativas de forma paginada")
    void deveListarPecasAtivasDeFormaPaginada() {
        var peca1 = new Peca(1L, "Filtro", "Filtro de óleo", new BigDecimal("30.00"), true);
        var peca2 = new Peca(2L, "Óleo", "Óleo sintético", new BigDecimal("59.90"), true);
        var page = new PaginatedResult<>(List.of(peca1, peca2), 0, 10, 2L, 1);

        when(repository.findAllActivePaginated(0, 10)).thenReturn(page);

        PaginatedResult<PecaResponse> result = useCase.execute(0, 10);

        assertThat(result.content()).hasSize(2);
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.content().get(0).id()).isEqualTo(1L);
        assertThat(result.content().get(1).id()).isEqualTo(2L);
        verify(repository).findAllActivePaginated(0, 10);
    }
}
