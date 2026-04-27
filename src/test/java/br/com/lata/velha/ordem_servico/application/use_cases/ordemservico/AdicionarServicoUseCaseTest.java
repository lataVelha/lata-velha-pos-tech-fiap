package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.ServicoRepository;
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
class AdicionarServicoUseCaseTest {

    @Mock private OrdemServicoRepository ordemServicoRepository;
    @Mock private ServicoRepository servicoRepository;
    @Mock private PecaRepository pecaRepository;

    @InjectMocks
    private AdicionarServicoUseCase useCase;

    private static final Long OS_ID = 1L;
    private static final Long SERVICO_ID = 10L;
    private static final Long PECA_ID = 20L;
    private static final Long EXEC_ID = 100L;

    private OrdemServico os;

    @BeforeEach
    void setUp() {
        os = new OrdemServico(OS_ID, 1L, 2L, "Barulho ao frear",
                StatusOrdemServico.EM_DIAGNOSTICO, LocalDateTime.now(), null, null, null, null,
                1L, null, new ArrayList<>());
    }

    private OrdemServico buildOsWithExecucao(Long servicoId) {
        ExecucaoServico exec = new ExecucaoServico(EXEC_ID, servicoId, OS_ID,
                StatusExecucaoServico.PENDENTE, new BigDecimal("150"),
                new HashSet<>(), null, null, null, null, LocalDateTime.now());
        return new OrdemServico(OS_ID, 1L, 2L, "Barulho ao frear",
                StatusOrdemServico.EM_DIAGNOSTICO, LocalDateTime.now(), null, null, null, null,
                1L, null, new ArrayList<>(List.of(exec)));
    }

    @Test
    @DisplayName("deve adicionar serviço sem peças e salvar duas vezes")
    void deveAdicionarServicoSemPecasComSucesso() {
        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(servicoRepository.getAllActiveById(any())).thenReturn(Set.of(new Servico(SERVICO_ID, "Troca de óleo", "desc")));
        when(ordemServicoRepository.save(any())).thenReturn(buildOsWithExecucao(SERVICO_ID));

        var input = new AdicionarServicoUseCase.Input(OS_ID, List.of(
                new AdicionarServicoUseCase.Input.ServicoAdicionar(SERVICO_ID, List.of(), new BigDecimal("150"))));

        useCase.execute(input);

        verify(ordemServicoRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("deve adicionar serviço com peças e associar preços corretamente")
    void deveAdicionarServicoComPecas() {
        var osComExec = buildOsWithExecucao(SERVICO_ID);
        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(servicoRepository.getAllActiveById(any())).thenReturn(Set.of(new Servico(SERVICO_ID, "Troca de óleo", "desc")));
        when(ordemServicoRepository.save(any())).thenReturn(osComExec);
        when(pecaRepository.getAllActiveByIds(any())).thenReturn(Set.of(buildPeca(PECA_ID, new BigDecimal("50.00"))));

        var input = new AdicionarServicoUseCase.Input(OS_ID, List.of(
                new AdicionarServicoUseCase.Input.ServicoAdicionar(SERVICO_ID,
                        List.of(new AdicionarServicoUseCase.Input.PecaNecessaria(PECA_ID, 2)),
                        new BigDecimal("150"))));

        useCase.execute(input);

        var execucoes = osComExec.getExecucaoServicos();
        assertThat(execucoes).hasSize(1);
        assertThat(execucoes.get(0).getPecas()).hasSize(1);
        var peca = execucoes.get(0).getPecas().iterator().next();
        assertThat(peca.getPecaId()).isEqualTo(PECA_ID);
        assertThat(peca.getQuantidadeSolicitada()).isEqualTo(2);
        assertThat(peca.getValorUnitarioPeca()).isEqualByComparingTo("50.00");
    }

    @Test
    @DisplayName("deve lançar exceção quando o mesmo serviço aparece duplicado no input")
    void deveLancarExcecaoQuandoServicoDuplicadoNoInput() {
        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);

        var input = new AdicionarServicoUseCase.Input(OS_ID, List.of(
                new AdicionarServicoUseCase.Input.ServicoAdicionar(SERVICO_ID, List.of(), new BigDecimal("150")),
                new AdicionarServicoUseCase.Input.ServicoAdicionar(SERVICO_ID, List.of(), new BigDecimal("200"))));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("duplicados");

        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve lançar exceção quando alguma peça informada não existe")
    void deveLancarExcecaoQuandoPecaNaoExiste() {
        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(servicoRepository.getAllActiveById(any())).thenReturn(Set.of(new Servico(SERVICO_ID, "Troca de óleo", "desc")));
        when(ordemServicoRepository.save(any())).thenReturn(buildOsWithExecucao(SERVICO_ID));
        when(pecaRepository.getAllActiveByIds(any())).thenReturn(Set.of());

        var input = new AdicionarServicoUseCase.Input(OS_ID, List.of(
                new AdicionarServicoUseCase.Input.ServicoAdicionar(SERVICO_ID,
                        List.of(new AdicionarServicoUseCase.Input.PecaNecessaria(PECA_ID, 1)),
                        new BigDecimal("150"))));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não existem");
    }

    @Test
    @DisplayName("deve adicionar múltiplos serviços distintos")
    void deveAdicionarMultiplosServicosDistintos() {
        Long servicoId2 = 11L;
        var osComExecs = new OrdemServico(OS_ID, 1L, 2L, "Barulho",
                StatusOrdemServico.EM_DIAGNOSTICO, LocalDateTime.now(), null, null, null, null,
                1L, null, new ArrayList<>(List.of(
                        new ExecucaoServico(EXEC_ID, SERVICO_ID, OS_ID, StatusExecucaoServico.PENDENTE,
                                new BigDecimal("150"), new HashSet<>(), null, null, null, null, LocalDateTime.now()),
                        new ExecucaoServico(101L, servicoId2, OS_ID, StatusExecucaoServico.PENDENTE,
                                new BigDecimal("100"), new HashSet<>(), null, null, null, null, LocalDateTime.now())
                )));

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(servicoRepository.getAllActiveById(any())).thenReturn(Set.of(
                new Servico(SERVICO_ID, "Troca de óleo", "desc"),
                new Servico(servicoId2, "Alinhamento", "desc")));
        when(ordemServicoRepository.save(any())).thenReturn(osComExecs);

        var input = new AdicionarServicoUseCase.Input(OS_ID, List.of(
                new AdicionarServicoUseCase.Input.ServicoAdicionar(SERVICO_ID, List.of(), new BigDecimal("150")),
                new AdicionarServicoUseCase.Input.ServicoAdicionar(servicoId2, List.of(), new BigDecimal("100"))));

        useCase.execute(input);

        assertThat(os.getExecucaoServicos()).hasSize(2);
        verify(ordemServicoRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("deve lançar exceção quando nenhum dos serviços solicitados existe")
    void deveLancarExcecaoQuandoNenhumServicoExiste() {
        when(ordemServicoRepository.getById(OS_ID)).thenReturn(os);
        when(servicoRepository.getAllActiveById(any())).thenReturn(Set.of());

        var input = new AdicionarServicoUseCase.Input(OS_ID, List.of(
                new AdicionarServicoUseCase.Input.ServicoAdicionar(SERVICO_ID, List.of(), new BigDecimal("150"))));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nenhum serviço solicitado existe");

        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve propagar exceção quando OS não encontrada")
    void devePropagateExcecaoQuandoOsNaoEncontrada() {
        when(ordemServicoRepository.getById(OS_ID)).thenThrow(new RuntimeException("OS não encontrada"));

        var input = new AdicionarServicoUseCase.Input(OS_ID, List.of(
                new AdicionarServicoUseCase.Input.ServicoAdicionar(SERVICO_ID, List.of(), new BigDecimal("150"))));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("OS não encontrada");

        verify(ordemServicoRepository, never()).save(any());
    }

    @Test
    @DisplayName("deve lançar exceção ao tentar adicionar serviço já existente na OS")
    void deveLancarExcecaoQuandoServicoJaAdicionadoNaOs() {
        var execExistente = new ExecucaoServico(EXEC_ID, SERVICO_ID, OS_ID,
                StatusExecucaoServico.PENDENTE, new BigDecimal("150"),
                new HashSet<>(), null, null, null, null, LocalDateTime.now());
        var osComServico = new OrdemServico(OS_ID, 1L, 2L, "Barulho",
                StatusOrdemServico.EM_DIAGNOSTICO, LocalDateTime.now(), null, null, null, null,
                1L, null, new ArrayList<>(List.of(execExistente)));

        when(ordemServicoRepository.getById(OS_ID)).thenReturn(osComServico);
        when(servicoRepository.getAllActiveById(any())).thenReturn(Set.of(new Servico(SERVICO_ID, "Troca de óleo", "desc")));

        var input = new AdicionarServicoUseCase.Input(OS_ID, List.of(
                new AdicionarServicoUseCase.Input.ServicoAdicionar(SERVICO_ID, List.of(), new BigDecimal("150"))));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Serviço já adicionado");
    }

    private Peca buildPeca(Long id, BigDecimal valor) {
        return new Peca(id, "Filtro", "desc", valor);
    }
}
