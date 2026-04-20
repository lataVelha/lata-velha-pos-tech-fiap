package br.com.lata.velha.ordem_servico.application.use_cases.peca;

import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarPecaPorIdUseCaseTest {

    @Mock
    private PecaRepository repository;

    @InjectMocks
    private BuscarPecaPorIdUseCase useCase;

    @Test
    @DisplayName("Deve buscar peça ativa por ID com sucesso")
    void deveBuscarPecaAtivaPorIdComSucesso() {
        var peca = new Peca(1L, "Disco de freio", "Disco dianteiro", new BigDecimal("220.00"), true);
        when(repository.getActiveById(1L)).thenReturn(peca);

        var result = useCase.execute(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.nome()).isEqualTo("Disco de freio");
        verify(repository).getActiveById(1L);
    }

    @Test
    @DisplayName("Deve falhar ao buscar peça inexistente")
    void deveFalharAoBuscarPecaInexistente() {
        when(repository.getActiveById(99L)).thenThrow(new IllegalArgumentException("Peça não encontrada"));

        assertThatThrownBy(() -> useCase.execute(99L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
