package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.*;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaEstoqueRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
class RetirarVeiculoUseCaseTest {

    @Mock private OrdemServicoRepository ordemServicoRepository;
    @Mock private FuncionarioRepository funcionarioRepository;
    @Mock private PecaEstoqueRepository pecaEstoqueRepository;
    @Mock private NotificarOrdemServicoUseCase notificarUseCase;
    @Mock private ProprietarioRepository proprietarioRepository;
    @Mock private VeiculoRepository veiculoRepository;

    @InjectMocks
    private RetirarVeiculoUseCase useCase;

    private static final Long OS_ID = 1L;
    private static final Long ATENDENTE_ID = 10L;
    private static final Long MECANICO_ID = 20L;
    private static final Long PROPRIETARIO_ID = 30L;
    private static final Long VEICULO_ID = 40L;

    private UserId userId;
    private Funcionario atendente;

    @BeforeEach
    void setUp() {
        userId = UserId.random();
        atendente = new Funcionario(ATENDENTE_ID, "Ana Atendente", null, null);
    }

    private OrdemServico buildOsFinalizada(List<ExecucaoServico> execucoes) {
        return new OrdemServico(
                OS_ID, PROPRIETARIO_ID, VEICULO_ID, "Barulho ao frear",
                StatusOrdemServico.FINALIZADA,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),
                null, null,
                ATENDENTE_ID, MECANICO_ID,
                new ArrayList<>(execucoes)
        );
    }

    private OrdemServico buildOsComStatus(StatusOrdemServico status) {
        return new OrdemServico(
                OS_ID, PROPRIETARIO_ID, VEICULO_ID, "Reclamação",
                status,
                LocalDateTime.now(), null, null, null, null,
                ATENDENTE_ID, MECANICO_ID,
                new ArrayList<>()
        );
    }

    private ExecucaoServico buildExecFinalizada() {
        return new ExecucaoServico(
                10L, 99L, OS_ID, StatusExecucaoServico.FINALIZADO,
                new BigDecimal("200.00"), new HashSet<>(),
                ATENDENTE_ID, MECANICO_ID,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now()
        );
    }

    private ExecucaoServico buildExecFinalizadaComPecaInstalada() {
        var peca = new PecaAlocada(
                100L, 5L, 10L, new BigDecimal("50.00"),
                2, 2, 0, 2,
                StatusPecaAlocada.INSTALADA, LocalDateTime.now()
        );
        return new ExecucaoServico(
                10L, 99L, OS_ID, StatusExecucaoServico.FINALIZADO,
                new BigDecimal("200.00"), Set.of(peca),
                ATENDENTE_ID, MECANICO_ID,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now()
        );
    }

    private Proprietario buildProprietario() {
        var proprietario = new Proprietario();
        proprietario.setId(PROPRIETARIO_ID);
        proprietario.setNome("João Proprietário");
        proprietario.setEmail("joao@email.com");
        proprietario.setDocumento(br.com.lata.velha.ordem_servico.domain.value_objects.Documento.of("529.982.247-25"));
        proprietario.setNumeroCelular(br.com.lata.velha.ordem_servico.domain.value_objects.NumeroCelular.of("11999999999"));
        return proprietario;
    }

    private Veiculo buildVeiculo() {
        return new Veiculo(VEICULO_ID, PROPRIETARIO_ID,
                br.com.lata.velha.ordem_servico.domain.value_objects.Placa.of("ABC1D23"),
                "Fiat", "Uno", 2020, "Prata");
    }

    // ------------------------------------------------------------------

    @Test
    @DisplayName("deve retirar veículo com sucesso quando OS está FINALIZADA")
    void deveRetirarVeiculoComSucesso() {
        var exec = buildExecFinalizada();
        var os = buildOsFinalizada(List.of(exec));
        var mecanico = new Funcionario(MECANICO_ID, "Carlos Mecânico", null, null);

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(atendente);
        when(ordemServicoRepository.save(any())).thenReturn(os);
        when(proprietarioRepository.getActiveById(PROPRIETARIO_ID)).thenReturn(buildProprietario());
        when(veiculoRepository.getActiveById(VEICULO_ID)).thenReturn(buildVeiculo());
        when(funcionarioRepository.getById(MECANICO_ID)).thenReturn(mecanico);

        var response = useCase.execute(OS_ID, userId);

        assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.ENTREGUE);
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(OS_ID);
        assertThat(response.status()).isEqualTo("ENTREGUE");
    }

    @Test
    @DisplayName("deve lançar exceção quando OS não está FINALIZADA")
    void deveLancarExcecaoQuandoOsNaoEstaFinalizada() {
        var os = buildOsComStatus(StatusOrdemServico.EM_EXECUCAO);

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(atendente);

        assertThatThrownBy(() -> useCase.execute(OS_ID, userId))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining(String.valueOf(OS_ID));

        verify(ordemServicoRepository, never()).save(any());
        verify(notificarUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("deve lançar exceção quando OS está RECEBIDA")
    void deveLancarExcecaoQuandoOsEstaRecebida() {
        var os = buildOsComStatus(StatusOrdemServico.RECEBIDA);

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(atendente);

        assertThatThrownBy(() -> useCase.execute(OS_ID, userId))
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve lançar exceção quando OS está APROVADA")
    void deveLancarExcecaoQuandoOsEstaAprovada() {
        var os = buildOsComStatus(StatusOrdemServico.APROVADA);

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(atendente);

        assertThatThrownBy(() -> useCase.execute(OS_ID, userId))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    @DisplayName("deve calcular totalServicos com base nas execuções FINALIZADAS")
    void deveCalcularTotalServicosComExecucoesFinalizadas() {
        var exec1 = buildExecFinalizada();
        var exec2 = new ExecucaoServico(
                11L, 98L, OS_ID, StatusExecucaoServico.RECUSADO,
                new BigDecimal("100.00"), new HashSet<>(),
                ATENDENTE_ID, null, null, null, null
        );
        var os = buildOsFinalizada(List.of(exec1, exec2));
        var mecanico = new Funcionario(MECANICO_ID, "Carlos", null, null);

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(atendente);
        when(ordemServicoRepository.save(any())).thenReturn(os);
        when(proprietarioRepository.getActiveById(PROPRIETARIO_ID)).thenReturn(buildProprietario());
        when(veiculoRepository.getActiveById(VEICULO_ID)).thenReturn(buildVeiculo());
        when(funcionarioRepository.getById(MECANICO_ID)).thenReturn(mecanico);

        var response = useCase.execute(OS_ID, userId);

        assertThat(response.totais().maoDeObraAprovada()).isEqualByComparingTo("200.00");
    }

    @Test
    @DisplayName("deve baixar estoque das peças instaladas ao retirar veículo")
    void deveBaixarEstoqueDasPecasInstaladas() {
        var exec = buildExecFinalizadaComPecaInstalada();
        var os = buildOsFinalizada(List.of(exec));
        var mecanico = new Funcionario(MECANICO_ID, "Carlos", null, null);

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(atendente);
        when(ordemServicoRepository.save(any())).thenReturn(os);
        when(proprietarioRepository.getActiveById(PROPRIETARIO_ID)).thenReturn(buildProprietario());
        when(veiculoRepository.getActiveById(VEICULO_ID)).thenReturn(buildVeiculo());
        when(funcionarioRepository.getById(MECANICO_ID)).thenReturn(mecanico);

        useCase.execute(OS_ID, userId);

        verify(pecaEstoqueRepository).baixarEstoque(5L, 2);
    }

    @Test
    @DisplayName("deve lançar exceção quando peça não está INSTALADA")
    void deveLancarExcecaoQuandoPecaNaoEstaInstalada() {
        var peca = new PecaAlocada(
                100L, 5L, 10L, new BigDecimal("50.00"),
                2, 2, 0, 0,
                StatusPecaAlocada.PARCIAL, LocalDateTime.now()
        );
        var exec = new ExecucaoServico(
                10L, 99L, OS_ID, StatusExecucaoServico.FINALIZADO,
                new BigDecimal("200.00"), Set.of(peca),
                ATENDENTE_ID, MECANICO_ID,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now()
        );
        var os = buildOsFinalizada(List.of(exec));

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(atendente);

        assertThatThrownBy(() -> useCase.execute(OS_ID, userId))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Peça não instalada");

        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve notificar após retirar veículo")
    void deveNotificarAposRetirarVeiculo() {
        var exec = buildExecFinalizada();
        var os = buildOsFinalizada(List.of(exec));
        var mecanico = new Funcionario(MECANICO_ID, "Carlos", null, null);

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(atendente);
        when(ordemServicoRepository.save(any())).thenReturn(os);
        when(proprietarioRepository.getActiveById(PROPRIETARIO_ID)).thenReturn(buildProprietario());
        when(veiculoRepository.getActiveById(VEICULO_ID)).thenReturn(buildVeiculo());
        when(funcionarioRepository.getById(MECANICO_ID)).thenReturn(mecanico);

        useCase.execute(OS_ID, userId);

        verify(notificarUseCase).execute(os);
    }

    @Test
    @DisplayName("deve propagar exceção quando OS não encontrada")
    void devePropagateExcecaoQuandoOsNaoEncontrada() {
        when(ordemServicoRepository.getById(OS_ID))
                .thenThrow(new RuntimeException("OS não encontrada"));

        assertThatThrownBy(() -> useCase.execute(OS_ID, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("OS não encontrada");

        verify(ordemServicoRepository, never()).save(any());
        verify(notificarUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("deve propagar exceção quando atendente não encontrado")
    void devePropagateExcecaoQuandoAtendenteNaoEncontrado() {
        var os = buildOsFinalizada(List.of());

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId))
                .thenThrow(new RuntimeException("Atendente não encontrado"));

        assertThatThrownBy(() -> useCase.execute(OS_ID, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Atendente não encontrado");

        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve baixar estoque de múltiplas peças instaladas")
    void deveBaixarEstoqueDasMultiplasPecasInstaladas() {
        var peca1 = new PecaAlocada(101L, 5L, 10L, new BigDecimal("50.00"),
                2, 2, 0, 2, StatusPecaAlocada.INSTALADA, LocalDateTime.now());
        var peca2 = new PecaAlocada(102L, 6L, 10L, new BigDecimal("30.00"),
                3, 3, 0, 3, StatusPecaAlocada.INSTALADA, LocalDateTime.now());
        var exec = new ExecucaoServico(
                10L, 99L, OS_ID, StatusExecucaoServico.FINALIZADO,
                new BigDecimal("200.00"), Set.of(peca1, peca2),
                ATENDENTE_ID, MECANICO_ID,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now()
        );
        var os = buildOsFinalizada(List.of(exec));
        var mecanico = new Funcionario(MECANICO_ID, "Carlos", null, null);

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(atendente);
        when(ordemServicoRepository.save(any())).thenReturn(os);
        when(proprietarioRepository.getActiveById(PROPRIETARIO_ID)).thenReturn(buildProprietario());
        when(veiculoRepository.getActiveById(VEICULO_ID)).thenReturn(buildVeiculo());
        when(funcionarioRepository.getById(MECANICO_ID)).thenReturn(mecanico);

        useCase.execute(OS_ID, userId);

        verify(pecaEstoqueRepository).baixarEstoque(5L, 2);
        verify(pecaEstoqueRepository).baixarEstoque(6L, 3);
    }

    @Test
    @DisplayName("deve retornar totalServicos zero quando não há execuções finalizadas")
    void deveRetornarTotalZeroSemExecucoesFinalizadas() {
        var os = buildOsFinalizada(List.of());
        var mecanico = new Funcionario(MECANICO_ID, "Carlos", null, null);

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(atendente);
        when(ordemServicoRepository.save(any())).thenReturn(os);
        when(proprietarioRepository.getActiveById(PROPRIETARIO_ID)).thenReturn(buildProprietario());
        when(veiculoRepository.getActiveById(VEICULO_ID)).thenReturn(buildVeiculo());
        when(funcionarioRepository.getById(MECANICO_ID)).thenReturn(mecanico);

        var response = useCase.execute(OS_ID, userId);

        assertThat(response.totais()).isNull();
    }

    @Test
    @DisplayName("deve mapear nome do mecânico responsável no response")
    void deveMapearNomeMecanicoResponsavel() {
        var exec = buildExecFinalizada();
        var os = buildOsFinalizada(List.of(exec));
        var mecanico = new Funcionario(MECANICO_ID, "Carlos Mecânico", null, null);

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(atendente);
        when(ordemServicoRepository.save(any())).thenReturn(os);
        when(proprietarioRepository.getActiveById(PROPRIETARIO_ID)).thenReturn(buildProprietario());
        when(veiculoRepository.getActiveById(VEICULO_ID)).thenReturn(buildVeiculo());
        when(funcionarioRepository.getById(MECANICO_ID)).thenReturn(mecanico);

        var response = useCase.execute(OS_ID, userId);

        assertThat(response.mecanico().nome()).isEqualTo("Carlos Mecânico");
        assertThat(response.atendente().nome()).isEqualTo("Ana Atendente");
    }
}
