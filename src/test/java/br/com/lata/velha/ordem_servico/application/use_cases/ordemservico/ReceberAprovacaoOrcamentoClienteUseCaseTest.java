package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceberAprovacaoOrcamentoClienteUseCaseTest {

    @Mock private ReceberAprovacaoOrcamentoClienteGateway gateway;

    private ReceberAprovacaoOrcamentoClienteUseCase useCase;

    private static final Long OS_ID = 1L;
    private static final Long EXEC_ID_1 = 10L;
    private static final Long EXEC_ID_2 = 11L;

    @BeforeEach
    void setUp() {
        useCase = new ReceberAprovacaoOrcamentoClienteUseCase(gateway);
    }

    private ExecucaoServico buildExecPendente(Long id) {
        return new ExecucaoServico(id, 99L, OS_ID, StatusExecucaoServico.PENDENTE,
                new BigDecimal("150"), new HashSet<>(), null, null,
                null, null, LocalDateTime.now());
    }

    private OrdemServico buildOsAguardandoAprovacao(List<ExecucaoServico> execucoes) {
        return new OrdemServico(OS_ID, 1L, 2L, "Barulho ao frear",
                StatusOrdemServico.AGUARDANDO_APROVACAO,
                LocalDateTime.now(), null, null, null, null,
                3L, null, new ArrayList<>(execucoes));
    }

    @Test
    @DisplayName("deve aprovar OS quando pelo menos um serviço é aprovado")
    void deveAprovarOsQuandoPeloMenosUmServicoAprovado() {
        var exec1 = buildExecPendente(EXEC_ID_1);
        var exec2 = buildExecPendente(EXEC_ID_2);
        var os = buildOsAguardandoAprovacao(List.of(exec1, exec2));

        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        when(gateway.getEstoquePorPecaIds(any())).thenReturn(List.of());
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        var input = new ReceberAprovacaoOrcamentoClienteUseCase.Input(OS_ID, List.of(
                new ReceberAprovacaoOrcamentoClienteUseCase.Input.ServicoAprovacao(EXEC_ID_1, StatusExecucaoServico.APROVADO),
                new ReceberAprovacaoOrcamentoClienteUseCase.Input.ServicoAprovacao(EXEC_ID_2, StatusExecucaoServico.RECUSADO)
        ));

        useCase.execute(input);

        assertThat(exec1.getStatus()).isEqualTo(StatusExecucaoServico.APROVADO);
        assertThat(exec2.getStatus()).isEqualTo(StatusExecucaoServico.RECUSADO);
        assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.APROVADA);
    }

    @Test
    @DisplayName("deve reprovar OS quando todos os serviços são recusados")
    void deveReprovarOsQuandoTodosServicosRecusados() {
        var exec1 = buildExecPendente(EXEC_ID_1);
        var exec2 = buildExecPendente(EXEC_ID_2);
        var os = buildOsAguardandoAprovacao(List.of(exec1, exec2));

        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        when(gateway.getEstoquePorPecaIds(any())).thenReturn(List.of());
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        var input = new ReceberAprovacaoOrcamentoClienteUseCase.Input(OS_ID, List.of(
                new ReceberAprovacaoOrcamentoClienteUseCase.Input.ServicoAprovacao(EXEC_ID_1, StatusExecucaoServico.RECUSADO),
                new ReceberAprovacaoOrcamentoClienteUseCase.Input.ServicoAprovacao(EXEC_ID_2, StatusExecucaoServico.RECUSADO)
        ));

        useCase.execute(input);

        assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.REPROVADA);
    }

    @Test
    @DisplayName("deve salvar OS após processar aprovação")
    void deveSalvarOsAposProcessarAprovacao() {
        var exec = buildExecPendente(EXEC_ID_1);
        var os = buildOsAguardandoAprovacao(List.of(exec));

        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        when(gateway.getEstoquePorPecaIds(any())).thenReturn(List.of());
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        var input = new ReceberAprovacaoOrcamentoClienteUseCase.Input(OS_ID, List.of(
                new ReceberAprovacaoOrcamentoClienteUseCase.Input.ServicoAprovacao(EXEC_ID_1, StatusExecucaoServico.APROVADO)
        ));

        useCase.execute(input);

        verify(gateway).salvarOrdemServico(os);
    }

    @Test
    @DisplayName("deve reservar peças no estoque quando serviço é aprovado")
    void deveReservarPecasQuandoServicoAprovado() {
        Long pecaId = 100L;
        var pecaAlocada = new PecaAlocada(null, pecaId, EXEC_ID_1, new BigDecimal("50.00"),
                2, 0, 0, 0, StatusPecaAlocada.PENDENTE, LocalDateTime.now());
        var exec = new ExecucaoServico(EXEC_ID_1, 99L, OS_ID, StatusExecucaoServico.PENDENTE,
                new BigDecimal("150"), new HashSet<>(Set.of(pecaAlocada)), null, null,
                null, null, LocalDateTime.now());
        var os = buildOsAguardandoAprovacao(List.of(exec));

        var estoque = new PecaEstoque(pecaId, 10, 0);
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        when(gateway.getEstoquePorPecaIds(any())).thenReturn(List.of(estoque));
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        var input = new ReceberAprovacaoOrcamentoClienteUseCase.Input(OS_ID, List.of(
                new ReceberAprovacaoOrcamentoClienteUseCase.Input.ServicoAprovacao(EXEC_ID_1, StatusExecucaoServico.APROVADO)
        ));

        useCase.execute(input);

        verify(gateway).salvarEstoques(any());
    }

    @Test
    @DisplayName("deve lançar exceção quando serviço não pertence à OS")
    void deveLancarExcecaoQuandoServicoNaoPertenceAOs() {
        var exec = buildExecPendente(EXEC_ID_1);
        var os = buildOsAguardandoAprovacao(List.of(exec));

        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);

        Long execIdInvalido = 999L;
        var input = new ReceberAprovacaoOrcamentoClienteUseCase.Input(OS_ID, List.of(
                new ReceberAprovacaoOrcamentoClienteUseCase.Input.ServicoAprovacao(execIdInvalido, StatusExecucaoServico.APROVADO)
        ));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.valueOf(OS_ID));

        verify(gateway, never()).salvarOrdemServico(any());
    }

    @Test
    @DisplayName("deve lançar exceção quando peça não tem registro de estoque")
    void deveLancarExcecaoQuandoPecaSemRegistroDeEstoque() {
        Long pecaId = 100L;
        var pecaAlocada = new PecaAlocada(null, pecaId, EXEC_ID_1, new BigDecimal("50.00"),
                2, 0, 0, 0, StatusPecaAlocada.PENDENTE, LocalDateTime.now());
        var exec = new ExecucaoServico(EXEC_ID_1, 99L, OS_ID, StatusExecucaoServico.PENDENTE,
                new BigDecimal("150"), new HashSet<>(Set.of(pecaAlocada)), null, null,
                null, null, LocalDateTime.now());
        var os = buildOsAguardandoAprovacao(List.of(exec));

        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        when(gateway.getEstoquePorPecaIds(any())).thenReturn(List.of());

        var input = new ReceberAprovacaoOrcamentoClienteUseCase.Input(OS_ID, List.of(
                new ReceberAprovacaoOrcamentoClienteUseCase.Input.ServicoAprovacao(EXEC_ID_1, StatusExecucaoServico.APROVADO)
        ));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("estoque");

        verify(gateway, never()).salvarOrdemServico(any());
    }

    @Test
    @DisplayName("deve propagar exceção quando OS não encontrada")
    void devePropagateExcecaoQuandoOsNaoEncontrada() {
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID))
                .thenThrow(new RuntimeException("OS não encontrada"));

        var input = new ReceberAprovacaoOrcamentoClienteUseCase.Input(OS_ID, List.of(
                new ReceberAprovacaoOrcamentoClienteUseCase.Input.ServicoAprovacao(EXEC_ID_1, StatusExecucaoServico.APROVADO)
        ));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("OS não encontrada");

        verify(gateway, never()).salvarOrdemServico(any());
    }
}
