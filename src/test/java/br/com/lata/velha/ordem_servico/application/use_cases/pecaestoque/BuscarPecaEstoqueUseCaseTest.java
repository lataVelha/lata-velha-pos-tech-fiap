package br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque;

import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.PecaNotFoundException;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarPecaEstoqueUseCaseTest {

    @Mock
    private PecaRepository pecaRepository;

    @Mock
    private PecaEstoqueRepository pecaEstoqueRepository;

    @InjectMocks
    private BuscarPecaEstoqueUseCase useCase;

    @Test
    void deveBuscarEstoqueComSucesso() {
        PecaEstoque estoque = new PecaEstoque(1L, 10, 10);

        when(pecaRepository.existsActiveById(1L)).thenReturn(true);
        when(pecaEstoqueRepository.findByPecaId(1L)).thenReturn(Optional.of(estoque));

        var result = useCase.execute(1L);

        assertThat(result.pecaId()).isEqualTo(1L);
        assertThat(result.quantidadeArmazenada()).isEqualTo(10);
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueNaoExiste() {
        when(pecaRepository.existsActiveById(1L)).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(1L))
                .isInstanceOf(PecaNotFoundException.class);
    }
}
