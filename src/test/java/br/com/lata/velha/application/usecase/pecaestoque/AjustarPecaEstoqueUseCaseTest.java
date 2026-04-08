package br.com.lata.velha.application.usecase.pecaestoque;

import br.com.lata.velha.application.assembler.PecaEstoqueAssembler;
import br.com.lata.velha.application.dto.request.AjustarPecaEstoqueRequest;
import br.com.lata.velha.application.dto.response.PecaEstoqueResponse;
import br.com.lata.velha.domain.model.Peca;
import br.com.lata.velha.domain.model.PecaEstoque;
import br.com.lata.velha.domain.repository.PecaEstoqueRepository;
import br.com.lata.velha.domain.repository.PecaRepository;
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
class AjustarPecaEstoqueUseCaseTest {

    @Mock
    private PecaRepository pecaRepository;

    @Mock
    private PecaEstoqueRepository pecaEstoqueRepository;

    @Mock
    private PecaEstoqueAssembler assembler;

    @InjectMocks
    private AjustarPecaEstoqueUseCase useCase;

    @Test
    void deveAjustarSaldoComSucesso() {
        Peca peca = new Peca(1L, "Filtro", "Filtro de óleo", new BigDecimal("30.00"), true);
        PecaEstoque estoque = new PecaEstoque(1L, 10);
        AjustarPecaEstoqueRequest request = new AjustarPecaEstoqueRequest(4);

        when(pecaRepository.findActiveById(1L)).thenReturn(peca);
        when(pecaEstoqueRepository.findByPecaId(1L)).thenReturn(estoque);
        when(pecaEstoqueRepository.save(any(PecaEstoque.class))).thenAnswer(i -> i.getArgument(0));
        when(assembler.toResponse(any(PecaEstoque.class))).thenAnswer(i -> {
            PecaEstoque e = i.getArgument(0);
            return new PecaEstoqueResponse(e.getPecaId(), e.getQuantidadeArmazenada());
        });

        PecaEstoqueResponse response = useCase.execute(1L, request);

        assertThat(response.quantidadeArmazenada()).isEqualTo(4);
    }
}
