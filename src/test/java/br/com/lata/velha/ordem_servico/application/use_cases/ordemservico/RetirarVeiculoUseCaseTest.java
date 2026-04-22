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
class RetirarVeiculoUseCaseTest {

    @Mock private OrdemServicoRepository ordemServicoRepository;
    @Mock private FuncionarioRepository funcionarioRepository;
    @Mock private NotificarOrdemServicoUseCase notificarUseCase;

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

    private OrdemServico buildOs(StatusOrdemServico status) {
        return new OrdemServico(
                OS_ID, PROPRIETARIO_ID, VEICULO_ID, "Barulho ao frear",
                status,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),
                null, null,
                ATENDENTE_ID, MECANICO_ID,
                new ArrayList<>()
        );
    }

    @Test
    @DisplayName("deve retirar veículo com sucesso quando OS está FINALIZADA")
    void deveRetirarVeiculoQuandoOsFinalizada() {
        var os = buildOs(StatusOrdemServico.FINALIZADA);
        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(atendente);
        when(ordemServicoRepository.save(any())).thenReturn(os);

        useCase.execute(OS_ID, userId);

        assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.ENTREGUE);
    }

    @Test
    @DisplayName("deve retirar veículo com sucesso quando OS está REPROVADA")
    void deveRetirarVeiculoQuandoOsReprovada() {
        var os = buildOs(StatusOrdemServico.REPROVADA);
        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(atendente);
        when(ordemServicoRepository.save(any())).thenReturn(os);

        useCase.execute(OS_ID, userId);

        assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.ENTREGUE);
    }

    @Test
    @DisplayName("deve salvar OS após retirar veículo")
    void deveSalvarOsAposRetirarVeiculo() {
        var os = buildOs(StatusOrdemServico.FINALIZADA);
        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(atendente);
        when(ordemServicoRepository.save(any())).thenReturn(os);

        useCase.execute(OS_ID, userId);

        verify(ordemServicoRepository).save(os);
    }

    @Test
    @DisplayName("deve notificar após retirar veículo")
    void deveNotificarAposRetirarVeiculo() {
        var os = buildOs(StatusOrdemServico.FINALIZADA);
        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(atendente);
        when(ordemServicoRepository.save(any())).thenReturn(os);

        useCase.execute(OS_ID, userId);

        verify(notificarUseCase).execute(os);
    }

    @Test
    @DisplayName("deve lançar exceção quando OS não está FINALIZADA nem REPROVADA")
    void deveLancarExcecaoQuandoOsNaoEstaEmStatusValido() {
        var os = buildOs(StatusOrdemServico.EM_EXECUCAO);
        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(atendente);

        assertThatThrownBy(() -> useCase.execute(OS_ID, userId))
                .isInstanceOf(IllegalStateException.class);

        verify(ordemServicoRepository, never()).save(any());
        verify(notificarUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("deve lançar exceção quando OS está RECEBIDA")
    void deveLancarExcecaoQuandoOsEstaRecebida() {
        var os = buildOs(StatusOrdemServico.RECEBIDA);
        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(atendente);

        assertThatThrownBy(() -> useCase.execute(OS_ID, userId))
                .isInstanceOf(IllegalStateException.class);

        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve lançar exceção quando OS está APROVADA")
    void deveLancarExcecaoQuandoOsEstaAprovada() {
        var os = buildOs(StatusOrdemServico.APROVADA);
        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(atendente);

        assertThatThrownBy(() -> useCase.execute(OS_ID, userId))
                .isInstanceOf(IllegalStateException.class);

        verify(ordemServicoRepository, never()).save(any());
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
        var os = buildOs(StatusOrdemServico.FINALIZADA);
        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(funcionarioRepository.getByUserId(userId))
                .thenThrow(new RuntimeException("Atendente não encontrado"));

        assertThatThrownBy(() -> useCase.execute(OS_ID, userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Atendente não encontrado");

        verify(ordemServicoRepository, never()).save(any());
    }
}
