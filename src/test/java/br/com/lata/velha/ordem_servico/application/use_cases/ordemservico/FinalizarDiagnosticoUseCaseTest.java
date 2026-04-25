package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinalizarDiagnosticoUseCaseTest {

    @Mock private OrdemServicoRepository ordemServicoRepository;
    @Mock private FuncionarioRepository funcionarioRepository;
    @Mock private NotificarOrdemServicoUseCase notificarUseCase;

    @InjectMocks
    private FinalizarDiagnosticoUseCase useCase;

    private static final Long OS_ID = 1L;
    private static final Long ATENDENTE_ID = 10L;
    private static final Long MECANICO_ID = 20L;
    private static final Long PROPRIETARIO_ID = 30L;
    private static final Long VEICULO_ID = 40L;

    private UserId userId;

    @BeforeEach
    void setUp() {
        userId = UserId.random();
    }

    private OrdemServico buildOs(StatusOrdemServico status) {
        return new OrdemServico(
                OS_ID, PROPRIETARIO_ID, VEICULO_ID, "Barulho ao frear",
                status,
                LocalDateTime.now(), null, null, null, null,
                ATENDENTE_ID, MECANICO_ID,
                new ArrayList<>()
        );
    }

    private FinalizarDiagnosticoUseCase.Input input() {
        return new FinalizarDiagnosticoUseCase.Input(OS_ID, userId);
    }

    @Test
    @DisplayName("deve finalizar diagnóstico sem serviços e ir direto para FINALIZADA")
    void deveFinalizarDiagnosticoSemServicosParaFinalizada() {
        var os = buildOs(StatusOrdemServico.EM_DIAGNOSTICO);
        when(ordemServicoRepository.getByIdWithExecucoesAndPecas(OS_ID)).thenReturn(os);
        when(funcionarioRepository.existsByUserId(userId)).thenReturn(true);
        when(ordemServicoRepository.save(any())).thenReturn(os);

        useCase.execute(input());

        assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.FINALIZADA);
    }

    @Test
    @DisplayName("deve salvar OS após finalizar diagnóstico")
    void deveSalvarOsAposFinalizarDiagnostico() {
        var os = buildOs(StatusOrdemServico.EM_DIAGNOSTICO);
        when(ordemServicoRepository.getByIdWithExecucoesAndPecas(OS_ID)).thenReturn(os);
        when(funcionarioRepository.existsByUserId(userId)).thenReturn(true);
        when(ordemServicoRepository.save(any())).thenReturn(os);

        useCase.execute(input());

        verify(ordemServicoRepository).save(os);
    }

    @Test
    @DisplayName("deve notificar após finalizar diagnóstico")
    void deveNotificarAposFinalizarDiagnostico() {
        var os = buildOs(StatusOrdemServico.EM_DIAGNOSTICO);
        when(ordemServicoRepository.getByIdWithExecucoesAndPecas(OS_ID)).thenReturn(os);
        when(funcionarioRepository.existsByUserId(userId)).thenReturn(true);
        when(ordemServicoRepository.save(any())).thenReturn(os);

        useCase.execute(input());

        verify(notificarUseCase).execute(os);
    }

    @Test
    @DisplayName("deve lançar IllegalArgumentException quando usuário não é funcionário")
    void deveLancarExcecaoQuandoUsuarioNaoEFuncionario() {
        var os = buildOs(StatusOrdemServico.EM_DIAGNOSTICO);
        when(ordemServicoRepository.getByIdWithExecucoesAndPecas(OS_ID)).thenReturn(os);
        when(funcionarioRepository.existsByUserId(userId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(input()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("funcionário");

        verify(ordemServicoRepository, never()).save(any());
        verify(notificarUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("deve lançar exceção quando OS não está EM_DIAGNOSTICO")
    void deveLancarExcecaoQuandoOsNaoEstaEmDiagnostico() {
        var os = buildOs(StatusOrdemServico.RECEBIDA);
        when(ordemServicoRepository.getByIdWithExecucoesAndPecas(OS_ID)).thenReturn(os);
        when(funcionarioRepository.existsByUserId(userId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("EM_DIAGNOSTICO");

        verify(ordemServicoRepository, never()).save(any());
        verify(notificarUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("deve lançar exceção quando OS já está AGUARDANDO_APROVACAO")
    void deveLancarExcecaoQuandoOsJaAguardandoAprovacao() {
        var os = buildOs(StatusOrdemServico.AGUARDANDO_APROVACAO);
        when(ordemServicoRepository.getByIdWithExecucoesAndPecas(OS_ID)).thenReturn(os);
        when(funcionarioRepository.existsByUserId(userId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input()))
                .isInstanceOf(IllegalStateException.class);

        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve propagar exceção quando OS não encontrada")
    void devePropagateExcecaoQuandoOsNaoEncontrada() {
        when(ordemServicoRepository.getByIdWithExecucoesAndPecas(OS_ID))
                .thenThrow(new RuntimeException("OS não encontrada"));

        assertThatThrownBy(() -> useCase.execute(input()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("OS não encontrada");

        verify(ordemServicoRepository, never()).save(any());
        verify(notificarUseCase, never()).execute(any());
    }
}
