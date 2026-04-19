package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.*;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AprovarOrdemServicoUseCaseTest {

    @Mock private OrdemServicoRepository ordemServicoRepository;
    @Mock private FuncionarioRepository funcionarioRepository;
    @Mock private PecaEstoqueRepository pecaEstoqueRepository;
    @Mock private NotificarOrdemServicoUseCase notificarUseCase;

    @InjectMocks
    private AprovarOrdemServicoUseCase useCase;

    private static final Long OS_ID = 1L;
    private static final Long FUNCIONARIO_ID = 2L;
    private static final Long EXECUCAO_ID = 10L;
    private static final Long PECA_ID = 20L;

    private Funcionario funcionario;

    @BeforeEach
    void setUp() {
        funcionario = new Funcionario(FUNCIONARIO_ID, "Ana Atendente", null, null);
    }

    private void stubFuncionario() {
        when(funcionarioRepository.getById(FUNCIONARIO_ID)).thenReturn(funcionario);
    }

    private ExecucaoServico buildExecucao(Long id) {
        ExecucaoServico exec = new ExecucaoServico(new Servico(99L, "Troca de óleo", "desc"), new BigDecimal("200"));
        exec.setId(id);
        return exec;
    }

    private PecaAlocada buildPecaAlocada(Long pecaId, Long execId, int solicitada) {
        return new PecaAlocada(null, pecaId, execId, solicitada, 0, 0, StatusPecaAlocada.ORCAMENTO, LocalDateTime.now());
    }

    private OrdemServico buildOs(List<ExecucaoServico> execucoes) {
        return new OrdemServico(OS_ID, 1L, 2L, "Barulho ao frear",
                StatusOrdemServico.AGUARDANDO_APROVACAO,
                LocalDateTime.now(), null, null, LocalDateTime.now(),
                3L, 4L, execucoes);
    }

    private void stubSave(OrdemServico os) {
        when(ordemServicoRepository.save(any(OrdemServico.class))).thenReturn(os);
    }

    @Test
    @DisplayName("deve aprovar OS e execuções com estoque suficiente")
    void deveAprovarOsEExecucoesComEstoqueSuficiente() {
        stubFuncionario();
        ExecucaoServico exec = buildExecucao(EXECUCAO_ID);
        PecaAlocada peca = buildPecaAlocada(PECA_ID, EXECUCAO_ID, 3);
        exec.adicionarPeca(peca);

        OrdemServico os = buildOs(List.of(exec));
        when(ordemServicoRepository.getByIdWithExecucoes(OS_ID)).thenReturn(os);
        when(pecaEstoqueRepository.findAllByPecaIds(any())).thenReturn(
                List.of(new PecaEstoque(PECA_ID, 10, 10)));
        stubSave(os);

        var input = new AprovarOrdemServicoUseCase.Input(OS_ID, FUNCIONARIO_ID,
                List.of(new AprovarOrdemServicoUseCase.Input.Servicos(EXECUCAO_ID, StatusExecucaoServico.APROVADO)));

        var output = useCase.execute(input);

        assertThat(output.status()).isEqualTo(StatusOrdemServico.EM_EXECUCAO.name());
        assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.APROVADO);
        assertThat(peca.getStatus()).isEqualTo(StatusPecaAlocada.RESERVADA);
        assertThat(peca.getQuantidadeReservada()).isEqualTo(3);
        assertThat(peca.getQuantidadeEncomendada()).isZero();
    }

    @Test
    @DisplayName("deve recusar execução quando não informada no input")
    void deveRecusarExecucaoNaoInformadaNoInput() {
        stubFuncionario();
        ExecucaoServico exec = buildExecucao(EXECUCAO_ID);
        OrdemServico os = buildOs(List.of(exec));
        when(ordemServicoRepository.getByIdWithExecucoes(OS_ID)).thenReturn(os);
        when(pecaEstoqueRepository.findAllByPecaIds(any())).thenReturn(List.of());
        stubSave(os);

        var input = new AprovarOrdemServicoUseCase.Input(OS_ID, FUNCIONARIO_ID, List.of());

        useCase.execute(input);

        assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.RECUSADO);
    }

    @Test
    @DisplayName("deve recusar execução explicitamente informada como RECUSADO")
    void deveRecusarExecucaoExplicitamente() {
        stubFuncionario();
        ExecucaoServico exec = buildExecucao(EXECUCAO_ID);
        OrdemServico os = buildOs(List.of(exec));
        when(ordemServicoRepository.getByIdWithExecucoes(OS_ID)).thenReturn(os);
        when(pecaEstoqueRepository.findAllByPecaIds(any())).thenReturn(List.of());
        stubSave(os);

        var input = new AprovarOrdemServicoUseCase.Input(OS_ID, FUNCIONARIO_ID,
                List.of(new AprovarOrdemServicoUseCase.Input.Servicos(EXECUCAO_ID, StatusExecucaoServico.RECUSADO)));

        useCase.execute(input);

        assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.RECUSADO);
    }

    @Test
    @DisplayName("deve reservar parcialmente e encomendar faltante quando estoque insuficiente")
    void deveReservarParcialmenteQuandoEstoqueInsuficiente() {
        stubFuncionario();
        ExecucaoServico exec = buildExecucao(EXECUCAO_ID);
        PecaAlocada peca = buildPecaAlocada(PECA_ID, EXECUCAO_ID, 5);
        exec.adicionarPeca(peca);

        OrdemServico os = buildOs(List.of(exec));
        when(ordemServicoRepository.getByIdWithExecucoes(OS_ID)).thenReturn(os);
        when(pecaEstoqueRepository.findAllByPecaIds(any())).thenReturn(
                List.of(new PecaEstoque(PECA_ID, 3, 3)));
        stubSave(os);

        var input = new AprovarOrdemServicoUseCase.Input(OS_ID, FUNCIONARIO_ID,
                List.of(new AprovarOrdemServicoUseCase.Input.Servicos(EXECUCAO_ID, StatusExecucaoServico.APROVADO)));

        useCase.execute(input);

        assertThat(peca.getStatus()).isEqualTo(StatusPecaAlocada.PARCIAL);
        assertThat(peca.getQuantidadeReservada()).isEqualTo(3);
        assertThat(peca.getQuantidadeEncomendada()).isEqualTo(2);
    }

    @Test
    @DisplayName("deve encomendar tudo quando não há estoque para a peça")
    void deveEncomendarTudoQuandoSemEstoque() {
        stubFuncionario();
        ExecucaoServico exec = buildExecucao(EXECUCAO_ID);
        PecaAlocada peca = buildPecaAlocada(PECA_ID, EXECUCAO_ID, 4);
        exec.adicionarPeca(peca);

        OrdemServico os = buildOs(List.of(exec));
        when(ordemServicoRepository.getByIdWithExecucoes(OS_ID)).thenReturn(os);
        when(pecaEstoqueRepository.findAllByPecaIds(any())).thenReturn(List.of());
        stubSave(os);

        var input = new AprovarOrdemServicoUseCase.Input(OS_ID, FUNCIONARIO_ID,
                List.of(new AprovarOrdemServicoUseCase.Input.Servicos(EXECUCAO_ID, StatusExecucaoServico.APROVADO)));

        useCase.execute(input);

        assertThat(peca.getStatus()).isEqualTo(StatusPecaAlocada.PARCIAL);
        assertThat(peca.getQuantidadeReservada()).isZero();
        assertThat(peca.getQuantidadeEncomendada()).isEqualTo(4);
    }

    @Test
    @DisplayName("deve salvar estoque atualizado após aprovação")
    void deveSalvarEstoqueAtualizado() {
        stubFuncionario();
        ExecucaoServico exec = buildExecucao(EXECUCAO_ID);
        PecaAlocada peca = buildPecaAlocada(PECA_ID, EXECUCAO_ID, 2);
        exec.adicionarPeca(peca);

        OrdemServico os = buildOs(List.of(exec));
        PecaEstoque estoque = new PecaEstoque(PECA_ID, 10, 10);
        when(ordemServicoRepository.getByIdWithExecucoes(OS_ID)).thenReturn(os);
        when(pecaEstoqueRepository.findAllByPecaIds(any())).thenReturn(List.of(estoque));
        when(pecaEstoqueRepository.saveAll(any())).thenReturn(List.of(estoque));
        stubSave(os);

        var input = new AprovarOrdemServicoUseCase.Input(OS_ID, FUNCIONARIO_ID,
                List.of(new AprovarOrdemServicoUseCase.Input.Servicos(EXECUCAO_ID, StatusExecucaoServico.APROVADO)));

        useCase.execute(input);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<PecaEstoque>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(pecaEstoqueRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).anyMatch(e -> e.getQuantidadeDisponivel() == 8);
    }

    @Test
    @DisplayName("deve chamar notificação após aprovar OS")
    void deveChamarNotificacaoAposAprovar() {
        stubFuncionario();
        ExecucaoServico exec = buildExecucao(EXECUCAO_ID);
        OrdemServico os = buildOs(List.of(exec));
        when(ordemServicoRepository.getByIdWithExecucoes(OS_ID)).thenReturn(os);
        when(pecaEstoqueRepository.findAllByPecaIds(any())).thenReturn(List.of());
        stubSave(os);

        var input = new AprovarOrdemServicoUseCase.Input(OS_ID, FUNCIONARIO_ID, List.of());

        useCase.execute(input);

        verify(notificarUseCase).execute(os);
    }

    @Test
    @DisplayName("deve retornar output com id, status e lista de serviços")
    void deveRetornarOutputCorreto() {
        stubFuncionario();
        ExecucaoServico exec = buildExecucao(EXECUCAO_ID);
        OrdemServico os = buildOs(List.of(exec));
        when(ordemServicoRepository.getByIdWithExecucoes(OS_ID)).thenReturn(os);
        when(pecaEstoqueRepository.findAllByPecaIds(any())).thenReturn(List.of());

        OrdemServico savedOs = buildOs(List.of(exec));
        when(ordemServicoRepository.save(any())).thenReturn(savedOs);

        var input = new AprovarOrdemServicoUseCase.Input(OS_ID, FUNCIONARIO_ID, List.of());

        var output = useCase.execute(input);

        assertThat(output.idOs()).isEqualTo(OS_ID);
        assertThat(output.servicos()).hasSize(1);
        assertThat(output.servicos().get(0).idServicoOs()).isEqualTo(EXECUCAO_ID);
    }

    @Test
    @DisplayName("deve propagar exceção quando OS não encontrada")
    void deveLancarExcecaoQuandoOsNaoEncontrada() {
        when(ordemServicoRepository.getByIdWithExecucoes(OS_ID))
                .thenThrow(new RuntimeException("OS não encontrada"));

        var input = new AprovarOrdemServicoUseCase.Input(OS_ID, FUNCIONARIO_ID, List.of());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("OS não encontrada");

        verify(ordemServicoRepository, never()).save(any());
        verify(notificarUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("deve propagar exceção quando funcionário não encontrado")
    void deveLancarExcecaoQuandoFuncionarioNaoEncontrado() {
        when(funcionarioRepository.getById(FUNCIONARIO_ID))
                .thenThrow(new RuntimeException("Funcionário não encontrado"));
        when(ordemServicoRepository.getByIdWithExecucoes(OS_ID))
                .thenReturn(buildOs(List.of()));

        var input = new AprovarOrdemServicoUseCase.Input(OS_ID, FUNCIONARIO_ID, List.of());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Funcionário não encontrado");

        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve aprovar múltiplas execuções com tratamentos distintos")
    void deveProcessarMultiplasExecucoesComStatusDistintos() {
        stubFuncionario();
        Long execId2 = 11L;
        ExecucaoServico execAprovada = buildExecucao(EXECUCAO_ID);
        ExecucaoServico execRecusada = buildExecucao(execId2);

        OrdemServico os = buildOs(new ArrayList<>(List.of(execAprovada, execRecusada)));
        when(ordemServicoRepository.getByIdWithExecucoes(OS_ID)).thenReturn(os);
        when(pecaEstoqueRepository.findAllByPecaIds(any())).thenReturn(List.of());
        stubSave(os);

        var input = new AprovarOrdemServicoUseCase.Input(OS_ID, FUNCIONARIO_ID, List.of(
                new AprovarOrdemServicoUseCase.Input.Servicos(EXECUCAO_ID, StatusExecucaoServico.APROVADO),
                new AprovarOrdemServicoUseCase.Input.Servicos(execId2, StatusExecucaoServico.RECUSADO)
        ));

        useCase.execute(input);

        assertThat(execAprovada.getStatus()).isEqualTo(StatusExecucaoServico.APROVADO);
        assertThat(execRecusada.getStatus()).isEqualTo(StatusExecucaoServico.RECUSADO);
    }
}
