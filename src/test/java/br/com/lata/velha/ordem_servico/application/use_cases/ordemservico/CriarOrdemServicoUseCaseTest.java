package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class CriarOrdemServicoUseCaseTest {

    @Mock private OrdemServicoRepository repository;
    @Mock private FuncionarioRepository funcionarioRepository;
    @Mock private ProprietarioRepository proprietarioRepository;
    @Mock private VeiculoRepository veiculoRepository;
    @Mock private NotificarOrdemServicoUseCase notificarUseCase;

    @InjectMocks
    private CriarOrdemServicoUseCase useCase;

    private Veiculo veiculo;
    private Proprietario proprietario;
    private Funcionario funcionario;
    private CriarOrdemServicoUseCase.Input input;
    private OrdemServico savedOs;
    private UserId userId;

    @BeforeEach
    void setUp() {
        userId = UserId.random();
        input = new CriarOrdemServicoUseCase.Input(3L, 4L, userId, "Barulho ao frear");

        veiculo = mock(Veiculo.class);
        proprietario = mock(Proprietario.class);
        funcionario = new Funcionario(2L, "Maria Atendente", null, null);

        savedOs = new OrdemServico(1L, 4L, 3L, "Barulho ao frear",
                StatusOrdemServico.RECEBIDA, LocalDateTime.now(), null, null, null, null,
                2L, null, new ArrayList<>());
    }

    private void stubHappyPath() {
        when(veiculo.getId()).thenReturn(3L);
        when(veiculo.getMarca()).thenReturn("Fiat");
        when(veiculo.getModelo()).thenReturn("Uno");
        when(proprietario.getId()).thenReturn(4L);
        when(proprietario.getNome()).thenReturn("João");
        when(proprietarioRepository.getActiveById(4L)).thenReturn(proprietario);
        when(veiculoRepository.getActiveByIdAndProprietarioId(3L, 4L)).thenReturn(veiculo);
        when(funcionarioRepository.getByUserId(userId)).thenReturn(funcionario);
        when(repository.save(any(OrdemServico.class))).thenReturn(savedOs);
    }

    @Test
    @DisplayName("deve criar OrdemServico com sucesso e retornar output com id")
    void deveCriarOrdemServicoComSucesso() {
        stubHappyPath();

        OrdemServicoResponse output = useCase.execute(input);

        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("deve criar entidade OrdemServico com status RECEBIDA e dados corretos")
    void deveCriarEntidadeComDadosCorretos() {
        stubHappyPath();

        ArgumentCaptor<OrdemServico> captor = ArgumentCaptor.forClass(OrdemServico.class);
        useCase.execute(input);

        verify(repository).save(captor.capture());
        OrdemServico created = captor.getValue();
        assertThat(created.getStatus()).isEqualTo(StatusOrdemServico.RECEBIDA);
        assertThat(created.getProprietarioId()).isEqualTo(4L);
        assertThat(created.getVeiculoId()).isEqualTo(3L);
        assertThat(created.getAtendenteInicioId()).isEqualTo(2L);
        assertThat(created.getReclamacaoProprietario()).isEqualTo("Barulho ao frear");
    }

    @Test
    @DisplayName("deve chamar notificação com a OrdemServico salva")
    void deveChamarNotificacaoAposSalvar() {
        stubHappyPath();

        useCase.execute(input);

        verify(notificarUseCase).execute(savedOs);
    }

    @Test
    @DisplayName("deve propagar exceção quando proprietário não encontrado")
    void deveLancarExcecaoQuandoProprietarioNaoEncontrado() {
        when(proprietarioRepository.getActiveById(4L)).thenThrow(new RuntimeException("Proprietário não encontrado"));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Proprietário não encontrado");

        verify(repository, never()).save(any());
        verify(notificarUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("deve propagar exceção quando veículo não pertence ao proprietário")
    void deveLancarExcecaoQuandoVeiculoNaoPertenceAoProprietario() {
        when(proprietario.getId()).thenReturn(4L);
        when(proprietarioRepository.getActiveById(4L)).thenReturn(proprietario);
        when(veiculoRepository.getActiveByIdAndProprietarioId(3L, 4L))
                .thenThrow(new RuntimeException("Veículo não encontrado"));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Veículo não encontrado");

        verify(repository, never()).save(any());
        verify(notificarUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("deve propagar exceção quando funcionário não encontrado")
    void deveLancarExcecaoQuandoFuncionarioNaoEncontrado() {
        when(proprietario.getId()).thenReturn(4L);
        when(proprietarioRepository.getActiveById(4L)).thenReturn(proprietario);
        when(veiculoRepository.getActiveByIdAndProprietarioId(3L, 4L)).thenReturn(veiculo);
        when(funcionarioRepository.getByUserId(userId)).thenThrow(new RuntimeException("Funcionário não encontrado"));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Funcionário não encontrado");

        verify(repository, never()).save(any());
        verify(notificarUseCase, never()).execute(any());
    }
}
