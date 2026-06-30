package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.entities.PecaEstoque;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import br.com.lata.velha.shared.domain.value_objects.UserId;
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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinalizarServicoUseCaseTest {

    @Mock private FinalizarServicoGateway gateway;
    @Mock private NotificarOrdemServicoUseCase notificarUseCase;

    private FinalizarServicoUseCase useCase;

    private static final Long OS_ID = 1L;
    private static final Long MECANICO_ID = 2L;
    private static final Long EXEC_ID = 10L;

    private Funcionario mecanico;
    private UserId userId;

    @BeforeEach
    void setUp() {
        useCase = new FinalizarServicoUseCase(gateway, notificarUseCase);
        mecanico = new Funcionario(MECANICO_ID, "Carlos Mecânico", null, null);
        userId = UserId.random();
    }

    private ExecucaoServico buildExecEmExecucao(Long id) {
        return new ExecucaoServico(id, 99L, OS_ID, StatusExecucaoServico.EM_EXECUCAO,
                new BigDecimal("150"), new HashSet<>(), 1L, MECANICO_ID,
                LocalDateTime.now(), null, LocalDateTime.now());
    }

    private ExecucaoServico buildExecEmExecucaoComPecas(Long id, Set<PecaAlocada> pecas) {
        return new ExecucaoServico(id, 99L, OS_ID, StatusExecucaoServico.EM_EXECUCAO,
                new BigDecimal("150"), new HashSet<>(pecas), 1L, MECANICO_ID,
                LocalDateTime.now(), null, LocalDateTime.now());
    }

    private PecaAlocada buildPecaReservada(Long pecaId, int quantidade) {
        return new PecaAlocada(null, pecaId, EXEC_ID, new BigDecimal("50.00"),
                quantidade, quantidade, 0, 0, StatusPecaAlocada.RESERVADA, LocalDateTime.now());
    }

    private ExecucaoServico buildExecAprovado(Long id) {
        return new ExecucaoServico(id, 88L, OS_ID, StatusExecucaoServico.APROVADO,
                new BigDecimal("100"), new HashSet<>(), 1L, null,
                null, null, LocalDateTime.now());
    }

    private OrdemServico buildOsEmExecucao(List<ExecucaoServico> execucoes) {
        return new OrdemServico(OS_ID, 1L, 2L, "Barulho ao frear",
                StatusOrdemServico.EM_EXECUCAO, LocalDateTime.now(), LocalDateTime.now(),
                null, null, null, 3L, MECANICO_ID, new ArrayList<>(execucoes));
    }

    @Test
    @DisplayName("deve finalizar serviço e notificar quando OS é finalizada")
    void deveFinalizarServicoENotificarQuandoOsEhFinalizada() {
        var exec = buildExecEmExecucao(EXEC_ID);
        var os = buildOsEmExecucao(List.of(exec));

        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(mecanico);
        when(gateway.getEstoquePorPecaIds(any())).thenReturn(List.of());
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        useCase.execute(new FinalizarServicoUseCase.Input(OS_ID, EXEC_ID, userId));

        assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.FINALIZADO);
        assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.FINALIZADA);
        verify(notificarUseCase).execute(os);
    }

    @Test
    @DisplayName("deve finalizar serviço sem notificar quando existem outros serviços pendentes")
    void deveFinalizarServicoSemNotificarQuandoOutrosServicosExistem() {
        var exec1 = buildExecEmExecucao(EXEC_ID);
        var exec2 = buildExecAprovado(11L);
        var os = buildOsEmExecucao(List.of(exec1, exec2));

        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(mecanico);
        when(gateway.getEstoquePorPecaIds(any())).thenReturn(List.of());
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        useCase.execute(new FinalizarServicoUseCase.Input(OS_ID, EXEC_ID, userId));

        assertThat(exec1.getStatus()).isEqualTo(StatusExecucaoServico.FINALIZADO);
        assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.EM_EXECUCAO);
        verify(notificarUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("deve salvar OS após finalizar serviço")
    void deveSalvarOsAposFinalizarServico() {
        var exec = buildExecEmExecucao(EXEC_ID);
        var os = buildOsEmExecucao(List.of(exec));

        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(mecanico);
        when(gateway.getEstoquePorPecaIds(any())).thenReturn(List.of());
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        useCase.execute(new FinalizarServicoUseCase.Input(OS_ID, EXEC_ID, userId));

        verify(gateway).salvarOrdemServico(os);
    }

    @Test
    @DisplayName("deve propagar exceção quando OS não encontrada")
    void devePropagateExcecaoQuandoOsNaoEncontrada() {
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID))
                .thenThrow(new RuntimeException("OS não encontrada"));

        assertThatThrownBy(() -> useCase.execute(new FinalizarServicoUseCase.Input(OS_ID, EXEC_ID, userId)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("OS não encontrada");

        verify(gateway, never()).salvarOrdemServico(any());
        verify(notificarUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("deve propagar exceção quando mecânico não encontrado")
    void devePropagateExcecaoQuandoMecanicoNaoEncontrado() {
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID))
                .thenReturn(buildOsEmExecucao(List.of(buildExecEmExecucao(EXEC_ID))));
        when(gateway.getFuncionarioPorUserId(userId)).thenThrow(new RuntimeException("Mecânico não encontrado"));

        assertThatThrownBy(() -> useCase.execute(new FinalizarServicoUseCase.Input(OS_ID, EXEC_ID, userId)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Mecânico não encontrado");

        verify(gateway, never()).salvarOrdemServico(any());
    }

    @Test
    @DisplayName("deve propagar exceção quando serviço não pertence à OS")
    void devePropagateExcecaoQuandoServicoNaoPertenceAOs() {
        var exec = buildExecEmExecucao(EXEC_ID);
        var os = buildOsEmExecucao(List.of(exec));

        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(mecanico);

        Long execIdInvalido = 999L;
        assertThatThrownBy(() -> useCase.execute(new FinalizarServicoUseCase.Input(OS_ID, execIdInvalido, userId)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.valueOf(execIdInvalido));

        verify(gateway, never()).salvarOrdemServico(any());
    }

    @Test
    @DisplayName("deve atribuir mecânico responsável ao finalizar última execução")
    void deveAtribuirMecanicoResponsavelAoFinalizar() {
        var exec = buildExecEmExecucao(EXEC_ID);
        var os = buildOsEmExecucao(List.of(exec));

        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(mecanico);
        when(gateway.getEstoquePorPecaIds(any())).thenReturn(List.of());
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        useCase.execute(new FinalizarServicoUseCase.Input(OS_ID, EXEC_ID, userId));

        assertThat(os.getMecanicoResponsavelId()).isEqualTo(MECANICO_ID);
    }

    @Test
    @DisplayName("deve retirar do estoque a quantidade solicitada de cada peça ao finalizar execução")
    void deveRetirarEstoqueAoFinalizarExecucaoComPecas() {
        Long pecaId = 100L;
        int quantidadeSolicitada = 2;
        var peca = buildPecaReservada(pecaId, quantidadeSolicitada);
        var exec = buildExecEmExecucaoComPecas(EXEC_ID, Set.of(peca));
        var os = buildOsEmExecucao(List.of(exec));

        var estoque = new PecaEstoque(pecaId, 10, 8);
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(mecanico);
        when(gateway.getEstoquePorPecaIds(any())).thenReturn(List.of(estoque));
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        useCase.execute(new FinalizarServicoUseCase.Input(OS_ID, EXEC_ID, userId));

        assertThat(estoque.getQuantidadeArmazenada()).isEqualTo(8);
        verify(gateway).salvarEstoques(any());
    }

    @Test
    @DisplayName("deve lançar exceção quando peça da execução não possui registro de estoque")
    void deveLancarExcecaoQuandoPecaNaoTemRegistroDeEstoque() {
        Long pecaId = 100L;
        var peca = buildPecaReservada(pecaId, 2);
        var exec = buildExecEmExecucaoComPecas(EXEC_ID, Set.of(peca));
        var os = buildOsEmExecucao(List.of(exec));

        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(mecanico);
        when(gateway.getEstoquePorPecaIds(any())).thenReturn(List.of());

        assertThatThrownBy(() -> useCase.execute(new FinalizarServicoUseCase.Input(OS_ID, EXEC_ID, userId)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("estoque");

        verify(gateway, never()).salvarEstoques(any());
        verify(gateway, never()).salvarOrdemServico(any());
    }

    @Test
    @DisplayName("não deve interagir com estoque quando execução não possui peças")
    void naoInterageComEstoqueQuandoExecucaoSemPecas() {
        var exec = buildExecEmExecucao(EXEC_ID);
        var os = buildOsEmExecucao(List.of(exec));

        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(mecanico);
        when(gateway.getEstoquePorPecaIds(any())).thenReturn(List.of());
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        useCase.execute(new FinalizarServicoUseCase.Input(OS_ID, EXEC_ID, userId));

        verify(gateway, never()).salvarEstoques(argThat(c -> !c.isEmpty()));
    }
}
