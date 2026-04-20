package br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque;

import br.com.lata.velha.ordem_servico.application.assemblers.PecaEstoqueAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.request.MovimentarPecaEstoqueRequest;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaidaPecaEstoqueUseCaseTest {

    @Mock
    private PecaRepository pecaRepository;

    @Mock
    private PecaEstoqueRepository pecaEstoqueRepository;

    @Mock
    private PecaEstoqueAssembler assembler;

    @InjectMocks
    private SaidaPecaEstoqueUseCase useCase;

    @Test
    void deveBaixarEstoqueComSucesso() {
        Peca peca = new Peca(1L, "Filtro", "Filtro de óleo", new BigDecimal("30.00"), true);
        PecaEstoque estoque = new PecaEstoque(1L, 10, 10);
        MovimentarPecaEstoqueRequest request = new MovimentarPecaEstoqueRequest(3);

        when(pecaRepository.getActiveById(1L)).thenReturn(peca);
        when(pecaEstoqueRepository.findByPecaId(1L)).thenReturn(Optional.of(estoque));
        when(pecaEstoqueRepository.save(any(PecaEstoque.class))).thenAnswer(i -> i.getArgument(0));
        when(assembler.toResponse(any(PecaEstoque.class))).thenAnswer(i -> {
            PecaEstoque e = i.getArgument(0);
            return new PecaEstoqueResponse(e.getPecaId(), e.getQuantidadeArmazenada(), e.getQuantidadeDisponivel());
        });

        PecaEstoqueResponse response = useCase.execute(1L, request);

        assertThat(response.quantidadeArmazenada()).isEqualTo(7);
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueInsuficiente() {
        Peca peca = new Peca(1L, "Filtro", "Filtro de óleo", new BigDecimal("30.00"), true);
        PecaEstoque estoque = new PecaEstoque(1L, 2, 2);
        MovimentarPecaEstoqueRequest request = new MovimentarPecaEstoqueRequest(3);

        when(pecaRepository.getActiveById(1L)).thenReturn(peca);
        when(pecaEstoqueRepository.findByPecaId(1L)).thenReturn(Optional.of(estoque));

        assertThatThrownBy(() -> useCase.execute(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Estoque insuficiente para a peça informada");
    }

    @Test
    void deveLancarExcecaoQuandoEstoqueNaoExiste() {
        Peca peca = new Peca(1L, "Filtro", "Filtro de óleo", new BigDecimal("30.00"), true);
        MovimentarPecaEstoqueRequest request = new MovimentarPecaEstoqueRequest(3);

        when(pecaRepository.getActiveById(1L)).thenReturn(peca);
        when(pecaEstoqueRepository.findByPecaId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Estoque da peça não encontrado");
    }
}
