package br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque;

import br.com.lata.velha.ordem_servico.application.assemblers.PecaEstoqueAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.response.PecaEstoqueResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarPecaEstoqueUseCaseTest {

    @Mock
    private PecaRepository pecaRepository;

    @Mock
    private PecaEstoqueRepository pecaEstoqueRepository;

    @Mock
    private PecaEstoqueAssembler assembler;

    @InjectMocks
    private BuscarPecaEstoqueUseCase useCase;

    @Test
    void deveBuscarEstoqueComSucesso() {
        Peca peca = new Peca(1L, "Filtro", "Filtro de óleo", new BigDecimal("30.00"), true);
        PecaEstoque estoque = new PecaEstoque(1L, 10);
        PecaEstoqueResponse response = new PecaEstoqueResponse(1L, 10);

        when(pecaRepository.findActiveById(1L)).thenReturn(peca);
        when(pecaEstoqueRepository.findByPecaId(1L)).thenReturn(estoque);
        when(assembler.toResponse(estoque)).thenReturn(response);

        PecaEstoqueResponse result = useCase.execute(1L);

        assertThat(result.pecaId()).isEqualTo(1L);
        assertThat(result.quantidadeArmazenada()).isEqualTo(10);
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueNaoExiste() {
        Peca peca = new Peca(1L, "Filtro", "Filtro de óleo", new BigDecimal("30.00"), true);

        when(pecaRepository.findActiveById(1L)).thenReturn(peca);
        when(pecaEstoqueRepository.findByPecaId(1L)).thenReturn(null);

        assertThatThrownBy(() -> useCase.execute(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Estoque da peça não encontrado");
    }
}
