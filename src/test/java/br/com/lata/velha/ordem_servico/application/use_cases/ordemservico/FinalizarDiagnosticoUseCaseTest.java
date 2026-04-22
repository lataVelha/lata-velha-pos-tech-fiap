package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.*;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import br.com.lata.velha.ordem_servico.domain.repositories.*;
import br.com.lata.velha.ordem_servico.domain.value_objects.Placa;
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
class FinalizarDiagnosticoUseCaseTest {

    @Mock private OrdemServicoRepository ordemServicoRepository;
    @Mock private FuncionarioRepository funcionarioRepository;
    @Mock private ProprietarioRepository proprietarioRepository;
    @Mock private VeiculoRepository veiculoRepository;
    @Mock private PecaRepository pecaRepository;
    @Mock private NotificarOrdemServicoUseCase notificarUseCase;

    @InjectMocks
    private FinalizarDiagnosticoUseCase useCase;

    private static final Long OS_ID = 1L;
    private static final Long ATENDENTE_ID = 10L;
    private static final Long MECANICO_ID = 20L;
    private static final Long PROPRIETARIO_ID = 30L;
    private static final Long VEICULO_ID = 40L;

    private UserId userId;
    private Funcionario mecanico;
    private Funcionario atendente;

    @BeforeEach
    void setUp() {
        userId = UserId.random();
        mecanico = new Funcionario(MECANICO_ID, "Carlos Mecânico", null, null);
        atendente = new Funcionario(ATENDENTE_ID, "Ana Atendente", null, null);
    }

    private OrdemServico buildOsEmDiagnostico(List<ExecucaoServico> execucoes) {
        return new OrdemServico(
                OS_ID, PROPRIETARIO_ID, VEICULO_ID, "Barulho ao frear",
                StatusOrdemServico.EM_DIAGNOSTICO,
                LocalDateTime.now(), null, null, null, null,
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

    private Proprietario buildProprietario() {
        var p = new Proprietario();
        p.setId(PROPRIETARIO_ID);
        p.setNome("João Proprietário");
        p.setEmail("joao@email.com");
        p.setDocumento(br.com.lata.velha.ordem_servico.domain.value_objects.Documento.of("529.982.247-25"));
        p.setNumeroCelular(br.com.lata.velha.ordem_servico.domain.value_objects.NumeroCelular.of("11999999999"));
        return p;
    }

    private Veiculo buildVeiculo() {
        return new Veiculo(VEICULO_ID, PROPRIETARIO_ID, Placa.of("ABC1D23"), "Honda", "Civic", 2022, "Preto");
    }

    // ------------------------------------------------------------------

    @Test
    @DisplayName("deve finalizar diagnóstico e mudar status para AGUARDANDO_APROVACAO")
    void deveFinalizarDiagnosticoComSucesso() {
        var os = buildOsEmDiagnostico(List.of());

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(mecanico);
        when(ordemServicoRepository.save(any())).thenReturn(os);
        when(funcionarioRepository.getById(ATENDENTE_ID)).thenReturn(atendente);
        when(proprietarioRepository.getActiveById(PROPRIETARIO_ID)).thenReturn(buildProprietario());
        when(veiculoRepository.getActiveById(VEICULO_ID)).thenReturn(buildVeiculo());

        var response = useCase.execute(OS_ID, userId);

        assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.AGUARDANDO_APROVACAO);
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(OS_ID);
        assertThat(response.status()).isEqualTo("AGUARDANDO_APROVACAO");
    }

    @Test
    @DisplayName("deve notificar após finalizar diagnóstico")
    void deveNotificarAposFinalizarDiagnostico() {
        var os = buildOsEmDiagnostico(List.of());

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(mecanico);
        when(ordemServicoRepository.save(any())).thenReturn(os);
        when(funcionarioRepository.getById(ATENDENTE_ID)).thenReturn(atendente);
        when(proprietarioRepository.getActiveById(PROPRIETARIO_ID)).thenReturn(buildProprietario());
        when(veiculoRepository.getActiveById(VEICULO_ID)).thenReturn(buildVeiculo());

        useCase.execute(OS_ID, userId);

        verify(notificarUseCase).execute(os);
    }

    @Test
    @DisplayName("deve salvar OS após finalizar diagnóstico")
    void deveSalvarOsAposFinalizarDiagnostico() {
        var os = buildOsEmDiagnostico(List.of());

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(mecanico);
        when(ordemServicoRepository.save(any())).thenReturn(os);
        when(funcionarioRepository.getById(ATENDENTE_ID)).thenReturn(atendente);
        when(proprietarioRepository.getActiveById(PROPRIETARIO_ID)).thenReturn(buildProprietario());
        when(veiculoRepository.getActiveById(VEICULO_ID)).thenReturn(buildVeiculo());

        useCase.execute(OS_ID, userId);

        verify(ordemServicoRepository).save(os);
    }

    @Test
    @DisplayName("deve lançar exceção quando OS não está EM_DIAGNOSTICO")
    void deveLancarExcecaoQuandoOsNaoEstaEmDiagnostico() {
        var os = buildOsComStatus(StatusOrdemServico.RECEBIDA);

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(mecanico);

        assertThatThrownBy(() -> useCase.execute(OS_ID, userId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("EM_DIAGNOSTICO");

        verify(ordemServicoRepository, never()).save(any());
        verify(notificarUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("deve lançar exceção quando OS está AGUARDANDO_APROVACAO")
    void deveLancarExcecaoQuandoOsJaEstaAguardandoAprovacao() {
        var os = buildOsComStatus(StatusOrdemServico.AGUARDANDO_APROVACAO);

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(mecanico);

        assertThatThrownBy(() -> useCase.execute(OS_ID, userId))
                .isInstanceOf(IllegalStateException.class);

        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve lançar exceção quando OS está APROVADA")
    void deveLancarExcecaoQuandoOsEstaAprovada() {
        var os = buildOsComStatus(StatusOrdemServico.APROVADA);

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(mecanico);

        assertThatThrownBy(() -> useCase.execute(OS_ID, userId))
                .isInstanceOf(IllegalStateException.class);
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
    @DisplayName("deve propagar exceção quando mecânico não encontrado")
    void devePropagateExcecaoQuandoMecanicoNaoEncontrado() {
        when(ordemServicoRepository.getById(OS_ID)).thenReturn(buildOsEmDiagnostico(List.of()));
        when(funcionarioRepository.getByUserId(userId))
                .thenThrow(new RuntimeException("Mecânico não encontrado"));

        assertThatThrownBy(() -> useCase.execute(OS_ID, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Mecânico não encontrado");

        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve mapear nomes do atendente e mecânico no response")
    void deveMapearNomesNoResponse() {
        var os = buildOsEmDiagnostico(List.of());

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(mecanico);
        when(ordemServicoRepository.save(any())).thenReturn(os);
        when(funcionarioRepository.getById(ATENDENTE_ID)).thenReturn(atendente);
        when(proprietarioRepository.getActiveById(PROPRIETARIO_ID)).thenReturn(buildProprietario());
        when(veiculoRepository.getActiveById(VEICULO_ID)).thenReturn(buildVeiculo());

        var response = useCase.execute(OS_ID, userId);

        assertThat(response.atendente().nome()).isEqualTo("Ana Atendente");
        assertThat(response.mecanico().nome()).isEqualTo("Carlos Mecânico");
    }

    @Test
    @DisplayName("deve mapear dados do proprietário e veículo no response")
    void deveMapearProprietarioEVeiculoNoResponse() {
        var os = buildOsEmDiagnostico(List.of());

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(mecanico);
        when(ordemServicoRepository.save(any())).thenReturn(os);
        when(funcionarioRepository.getById(ATENDENTE_ID)).thenReturn(atendente);
        when(proprietarioRepository.getActiveById(PROPRIETARIO_ID)).thenReturn(buildProprietario());
        when(veiculoRepository.getActiveById(VEICULO_ID)).thenReturn(buildVeiculo());

        var response = useCase.execute(OS_ID, userId);

        assertThat(response.proprietario().nome()).isEqualTo("João Proprietário");
        assertThat(response.veiculo().descricao()).isEqualTo("Honda Civic");
    }

    @Test
    @DisplayName("deve buscar mecânico responsável por execução de serviço no response")
    void deveBuscarNomeMecanicoDeExecucaoNoResponse() {
        var mecanicoServico = new Funcionario(50L, "Pedro Mecânico", null, null);
        var execucao = new ExecucaoServico(
                100L, 99L, OS_ID, StatusExecucaoServico.PENDENTE,
                new BigDecimal("150.00"), new HashSet<>(),
                null, 50L, null, null, null
        );
        var os = buildOsEmDiagnostico(List.of(execucao));

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(mecanico);
        when(ordemServicoRepository.save(any())).thenReturn(os);
        when(funcionarioRepository.getById(ATENDENTE_ID)).thenReturn(atendente);
        when(funcionarioRepository.getById(50L)).thenReturn(mecanicoServico);
        when(proprietarioRepository.getActiveById(PROPRIETARIO_ID)).thenReturn(buildProprietario());
        when(veiculoRepository.getActiveById(VEICULO_ID)).thenReturn(buildVeiculo());

        useCase.execute(OS_ID, userId);

        verify(funcionarioRepository).getById(50L);
    }

    @Test
    @DisplayName("deve buscar peças das execuções de serviço para compor o response")
    void deveBuscarPecasDasExecucoesNoResponse() {
        var pecaAlocada = new br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada(
                200L, 7L, 100L, new BigDecimal("49.90"),
                2, 2, 0, 0,
                StatusPecaAlocada.RESERVADA, LocalDateTime.now()
        );
        var execucao = new ExecucaoServico(
                100L, 99L, OS_ID, StatusExecucaoServico.PENDENTE,
                new BigDecimal("150.00"), Set.of(pecaAlocada),
                null, null, null, null, null
        );
        var os = buildOsEmDiagnostico(List.of(execucao));
        var peca = new Peca(7L, "Pastilha", "Pastilha dianteira", new BigDecimal("49.90"));

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(mecanico);
        when(ordemServicoRepository.save(any())).thenReturn(os);
        when(funcionarioRepository.getById(ATENDENTE_ID)).thenReturn(atendente);
        when(proprietarioRepository.getActiveById(PROPRIETARIO_ID)).thenReturn(buildProprietario());
        when(veiculoRepository.getActiveById(VEICULO_ID)).thenReturn(buildVeiculo());
        when(pecaRepository.getActiveById(7L)).thenReturn(peca);

        useCase.execute(OS_ID, userId);

        verify(pecaRepository).getActiveById(7L);
    }

    @Test
    @DisplayName("deve retornar reclamacao do cliente no response")
    void deveMapearReclamacaoClienteNoResponse() {
        var os = buildOsEmDiagnostico(List.of());

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(mecanico);
        when(ordemServicoRepository.save(any())).thenReturn(os);
        when(funcionarioRepository.getById(ATENDENTE_ID)).thenReturn(atendente);
        when(proprietarioRepository.getActiveById(PROPRIETARIO_ID)).thenReturn(buildProprietario());
        when(veiculoRepository.getActiveById(VEICULO_ID)).thenReturn(buildVeiculo());

        var response = useCase.execute(OS_ID, userId);

        assertThat(response.reclamacaoCliente()).isEqualTo("Barulho ao frear");
    }
}
