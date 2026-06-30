package br.com.lata.velha.ordem_servico.application.use_cases.pecaestoque;

import br.com.lata.velha.ordem_servico.application.dtos.request.MovimentarPecaEstoqueRequest;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import br.com.lata.velha.ordem_servico.domain.exceptions.not_found_exceptions.PecaNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntradaPecaEstoqueUseCaseTest {

    @Mock
    private EntradaPecaEstoqueGateway gateway;

    private static final Long PECA_ID = 1L;
    private static final Long EXECUCAO_ID = 99L;

    @Test
    @DisplayName("deve adicionar quantidade quando estoque já existe")
    void deveAdicionarQuantidadeQuandoEstoqueJaExiste() {
        PecaEstoque estoque = new PecaEstoque(PECA_ID, 10, 10);

        when(gateway.getEstoquePorPecaId(PECA_ID)).thenReturn(estoque);
        when(gateway.getPecasAlocadasPendentes(PECA_ID)).thenReturn(List.of());
        when(gateway.salvarEstoque(any())).thenAnswer(i -> i.getArgument(0));

        EntradaPecaEstoqueUseCase useCase = new EntradaPecaEstoqueUseCase(gateway);
        PecaEstoque response = useCase.execute(PECA_ID, new MovimentarPecaEstoqueRequest(5));

        assertThat(response.getQuantidadeArmazenada()).isEqualTo(15);
        assertThat(response.getQuantidadeDisponivel()).isEqualTo(15);
        verify(gateway, never()).getExecucaoServicoPorId(any());
    }

    @Test
    @DisplayName("deve criar estoque quando gateway retorna estoque vazio")
    void deveCriarEstoqueQuandoGatewayRetornaVazio() {
        PecaEstoque estoqueVazio = new PecaEstoque(PECA_ID, 0, 0);

        when(gateway.getEstoquePorPecaId(PECA_ID)).thenReturn(estoqueVazio);
        when(gateway.getPecasAlocadasPendentes(PECA_ID)).thenReturn(List.of());
        when(gateway.salvarEstoque(any())).thenAnswer(i -> i.getArgument(0));

        EntradaPecaEstoqueUseCase useCase = new EntradaPecaEstoqueUseCase(gateway);
        PecaEstoque response = useCase.execute(PECA_ID, new MovimentarPecaEstoqueRequest(5));

        assertThat(response.getQuantidadeArmazenada()).isEqualTo(5);
        assertThat(response.getQuantidadeDisponivel()).isEqualTo(5);
    }

    @Test
    @DisplayName("deve reservar peça pendente através da execução e reduzir estoque disponível")
    void deveReservarPecaPendentePelaExecucao() {
        PecaEstoque estoque = new PecaEstoque(PECA_ID, 0, 0);
        PecaAlocada pecaPendente = new PecaAlocada(5L, PECA_ID, EXECUCAO_ID, new BigDecimal("10.00"),
                4, 0, 4, 0, StatusPecaAlocada.ENCOMENDA, LocalDateTime.now());
        ExecucaoServico execucao = new ExecucaoServico(EXECUCAO_ID, 10L, 1L,
                StatusExecucaoServico.AGUARDANDO_PECA, new BigDecimal("100.00"),
                new HashSet<>(Set.of(pecaPendente)), null, null, null, null, LocalDateTime.now());

        when(gateway.getEstoquePorPecaId(PECA_ID)).thenReturn(estoque);
        when(gateway.getPecasAlocadasPendentes(PECA_ID)).thenReturn(List.of(pecaPendente));
        when(gateway.getExecucaoServicoPorId(EXECUCAO_ID)).thenReturn(execucao);
        when(gateway.salvarEstoque(any())).thenAnswer(i -> i.getArgument(0));

        EntradaPecaEstoqueUseCase useCase = new EntradaPecaEstoqueUseCase(gateway);
        PecaEstoque response = useCase.execute(PECA_ID, new MovimentarPecaEstoqueRequest(10));

        assertThat(response.getQuantidadeDisponivel()).isEqualTo(6);
        assertThat(pecaPendente.getQuantidadeReservada()).isEqualTo(4);
        verify(gateway).salvarExecucaoServico(execucao);
    }

    @Test
    @DisplayName("não deve consultar execuções quando não há pendentes")
    void naoDeveConsultarExecucoesQuandoNaoHaPendentes() {
        PecaEstoque estoque = new PecaEstoque(PECA_ID, 0, 0);

        when(gateway.getEstoquePorPecaId(PECA_ID)).thenReturn(estoque);
        when(gateway.getPecasAlocadasPendentes(PECA_ID)).thenReturn(List.of());
        when(gateway.salvarEstoque(any())).thenAnswer(i -> i.getArgument(0));

        EntradaPecaEstoqueUseCase useCase = new EntradaPecaEstoqueUseCase(gateway);
        useCase.execute(PECA_ID, new MovimentarPecaEstoqueRequest(10));

        verify(gateway, never()).getExecucaoServicoPorId(any());
    }

    @Test
    @DisplayName("deve lançar exceção quando peça não existe")
    void deveLancarExcecaoQuandoPecaNaoExiste() {
        when(gateway.getPecaAtivaPorId(99L)).thenThrow(PecaNotFoundException.fromId(99L));

        EntradaPecaEstoqueUseCase useCase = new EntradaPecaEstoqueUseCase(gateway);

        assertThatThrownBy(() -> useCase.execute(99L, new MovimentarPecaEstoqueRequest(5)))
                .isInstanceOf(PecaNotFoundException.class);
    }
}
