package br.com.lata.velha.ordemDeServico.application.useCases.pecaestoque;

import br.com.lata.velha.ordemDeServico.application.assemblers.PecaEstoqueAssembler;
import br.com.lata.velha.ordemDeServico.application.dtos.request.MovimentarPecaEstoqueRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.response.PecaEstoqueResponse;
import br.com.lata.velha.ordemDeServico.domain.entities.Peca;
import br.com.lata.velha.ordemDeServico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordemDeServico.domain.repositories.PecaEstoqueRepository;
import br.com.lata.velha.ordemDeServico.domain.repositories.PecaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntradaPecaEstoqueUseCaseTest {

    @Mock
    private PecaRepository pecaRepository;

    @Mock
    private PecaEstoqueRepository pecaEstoqueRepository;

    @Mock
    private PecaEstoqueAssembler assembler;

    @InjectMocks
    private EntradaPecaEstoqueUseCase useCase;

    @Test
    void deveAdicionarQuantidadeQuandoEstoqueJaExiste() {
        Peca peca = new Peca(1L, "Filtro", "Filtro de óleo", new BigDecimal("30.00"), true);
        PecaEstoque estoque = new PecaEstoque(1L, 10);
        MovimentarPecaEstoqueRequest request = new MovimentarPecaEstoqueRequest(5);

        when(pecaRepository.findActiveById(1L)).thenReturn(peca);
        when(pecaEstoqueRepository.findByPecaId(1L)).thenReturn(estoque);
        when(pecaEstoqueRepository.save(any(PecaEstoque.class))).thenAnswer(i -> i.getArgument(0));
        when(assembler.toResponse(any(PecaEstoque.class))).thenAnswer(i -> {
            PecaEstoque e = i.getArgument(0);
            return new PecaEstoqueResponse(e.getPecaId(), e.getQuantidadeArmazenada());
        });

        PecaEstoqueResponse response = useCase.execute(1L, request);

        assertThat(response.quantidadeArmazenada()).isEqualTo(15);
    }

    @Test
    void deveCriarEstoqueQuandoNaoExiste() {
        Peca peca = new Peca(1L, "Filtro", "Filtro de óleo", new BigDecimal("30.00"), true);
        MovimentarPecaEstoqueRequest request = new MovimentarPecaEstoqueRequest(5);

        when(pecaRepository.findActiveById(1L)).thenReturn(peca);
        when(pecaEstoqueRepository.findByPecaId(1L)).thenReturn(null);
        when(pecaEstoqueRepository.save(any(PecaEstoque.class))).thenAnswer(i -> i.getArgument(0));
        when(assembler.toResponse(any(PecaEstoque.class))).thenAnswer(i -> {
            PecaEstoque e = i.getArgument(0);
            return new PecaEstoqueResponse(e.getPecaId(), e.getQuantidadeArmazenada());
        });

        PecaEstoqueResponse response = useCase.execute(1L, request);

        assertThat(response.quantidadeArmazenada()).isEqualTo(5);
    }
}
