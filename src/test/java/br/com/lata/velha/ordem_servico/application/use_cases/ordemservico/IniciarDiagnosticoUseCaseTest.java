package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
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
class IniciarDiagnosticoUseCaseTest {

    @Mock private OrdemServicoRepository repository;
    @Mock private FuncionarioRepository funcionarioRepository;
    @Mock private NotificarOrdemServicoUseCase notificarUseCase;

    @InjectMocks
    private IniciarDiagnosticoUseCase useCase;

    private static final Long OS_ID = 1L;
    private static final Long MECANICO_ID = 2L;

    private Funcionario mecanico;
    private UserId userId;

    @BeforeEach
    void setUp() {
        mecanico = new Funcionario(MECANICO_ID, "Carlos Mecânico", null, null);
        userId = UserId.random();
    }

    private OrdemServico buildOs(StatusOrdemServico status) {
        return new OrdemServico(OS_ID, 1L, 2L, "Barulho ao frear",
                status, LocalDateTime.now(), null, null, LocalDateTime.now(), null,
                3L, null, new ArrayList<>());
    }

    @Test
    @DisplayName("deve iniciar diagnóstico e alterar status para EM_DIAGNOSTICO")
    void deveIniciarDiagnosticoComSucesso() {
        var os = buildOs(StatusOrdemServico.RECEBIDA);
        when(repository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(mecanico);
        when(repository.save(any())).thenReturn(os);

        useCase.execute(new IniciarDiagnosticoUseCase.Input(OS_ID, userId));

        assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.EM_DIAGNOSTICO);
        assertThat(os.getMecanicoResponsavelId()).isEqualTo(MECANICO_ID);
    }

    @Test
    @DisplayName("deve persistir a OS após iniciar diagnóstico")
    void deveSalvarOsAposIniciarDiagnostico() {
        var os = buildOs(StatusOrdemServico.RECEBIDA);
        when(repository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(mecanico);
        when(repository.save(any())).thenReturn(os);

        useCase.execute(new IniciarDiagnosticoUseCase.Input(OS_ID, userId));

        verify(repository).save(os);
    }

    @Test
    @DisplayName("deve notificar após iniciar diagnóstico")
    void deveNotificarAposIniciarDiagnostico() {
        var os = buildOs(StatusOrdemServico.RECEBIDA);
        when(repository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(mecanico);
        when(repository.save(any())).thenReturn(os);

        useCase.execute(new IniciarDiagnosticoUseCase.Input(OS_ID, userId));

        verify(notificarUseCase).execute(os);
    }

    @Test
    @DisplayName("deve lançar IllegalStateException quando OS não está em status RECEBIDA")
    void deveLancarExcecaoQuandoStatusInvalido() {
        var os = buildOs(StatusOrdemServico.EM_DIAGNOSTICO);
        when(repository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(mecanico);

        assertThatThrownBy(() -> useCase.execute(new IniciarDiagnosticoUseCase.Input(OS_ID, userId)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("RECEBIDA");

        verify(repository, never()).save(any());
        verify(notificarUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("deve propagar exceção quando OS não encontrada")
    void devePropagarExcecaoQuandoOsNaoEncontrada() {
        when(repository.getById(OS_ID)).thenThrow(new RuntimeException("OS não encontrada"));

        assertThatThrownBy(() -> useCase.execute(new IniciarDiagnosticoUseCase.Input(OS_ID, userId)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("OS não encontrada");

        verify(repository, never()).save(any());
        verify(notificarUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("deve propagar exceção quando mecânico não encontrado")
    void devePropagarExcecaoQuandoMecanicoNaoEncontrado() {
        when(repository.getById(OS_ID)).thenReturn(buildOs(StatusOrdemServico.RECEBIDA));
        when(funcionarioRepository.getByUserId(userId)).thenThrow(new RuntimeException("Funcionário não encontrado"));

        assertThatThrownBy(() -> useCase.execute(new IniciarDiagnosticoUseCase.Input(OS_ID, userId)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Funcionário não encontrado");

        verify(repository, never()).save(any());
        verify(notificarUseCase, never()).execute(any());
    }
}
