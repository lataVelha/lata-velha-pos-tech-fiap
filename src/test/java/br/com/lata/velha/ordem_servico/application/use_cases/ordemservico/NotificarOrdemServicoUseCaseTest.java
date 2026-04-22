package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.ports.EmailProvider;
import br.com.lata.velha.ordem_servico.application.ports.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.domain.entities.*;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.ServicoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
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
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificarOrdemServicoUseCaseTest {

    @Mock private EmailProvider emailProvider;
    @Mock private EmailTemplateProvider templateProvider;
    @Mock private ProprietarioRepository proprietarioRepository;
    @Mock private VeiculoRepository veiculoRepository;
    @Mock private PecaRepository pecaRepository;
    @Mock private ServicoRepository servicoRepository;

    @InjectMocks
    private NotificarOrdemServicoUseCase useCase;

    private static final Long OS_ID = 1L;
    private static final Long PROP_ID = 2L;
    private static final Long VEICULO_ID = 3L;

    private Proprietario proprietario;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        proprietario = new Proprietario(PROP_ID, "João Silva", "joao@example.com", null, null, null);
        veiculo = new Veiculo(VEICULO_ID, PROP_ID, null, "Honda", "Civic", 2022, "Prata");

        when(proprietarioRepository.getActiveById(PROP_ID)).thenReturn(proprietario);
        when(veiculoRepository.getActiveById(VEICULO_ID)).thenReturn(veiculo);
        when(templateProvider.render(anyString(), anyMap())).thenReturn("<html>email</html>");
    }

    private OrdemServico buildOs(StatusOrdemServico status) {
        return buildOs(status, new ArrayList<>());
    }

    private OrdemServico buildOs(StatusOrdemServico status, List<ExecucaoServico> execucoes) {
        return new OrdemServico(OS_ID, PROP_ID, VEICULO_ID, "Barulho ao frear",
                status, LocalDateTime.now(), null, null, null, null,
                1L, null, execucoes);
    }

    private ExecucaoServico buildExec(Long id, StatusExecucaoServico status) {
        return new ExecucaoServico(id, 99L, OS_ID, status, new BigDecimal("150"),
                new HashSet<>(), null, null, null, null, LocalDateTime.now());
    }

    @Test
    @DisplayName("deve enviar email com sucesso no caminho feliz")
    void deveEnviarEmailComSucesso() {
        useCase.execute(buildOs(StatusOrdemServico.RECEBIDA));

        verify(emailProvider).send(eq("joao@example.com"), anyString(), anyString());
        verify(templateProvider).render(eq("os-notificacao"), anyMap());
    }

    @Test
    @DisplayName("deve não propagar exceção quando envio de email falha")
    void deveNaoPropagateExcecaoQuandoEmailFalha() {
        doThrow(new RuntimeException("SMTP error")).when(emailProvider).send(any(), any(), any());

        assertThatNoException().isThrownBy(() -> useCase.execute(buildOs(StatusOrdemServico.RECEBIDA)));
    }

    @Test
    @DisplayName("deve não propagar exceção quando template falha")
    void deveNaoPropagateExcecaoQuandoTemplateFalha() {
        when(templateProvider.render(anyString(), anyMap())).thenThrow(new RuntimeException("Template error"));

        assertThatNoException().isThrownBy(() -> useCase.execute(buildOs(StatusOrdemServico.RECEBIDA)));
        verify(emailProvider, never()).send(any(), any(), any());
    }

    @Test
    @DisplayName("deve incluir nome do proprietário, número da OS e veículo nas variáveis")
    void deveIncluirVariaveisBasicasNoTemplate() {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

        useCase.execute(buildOs(StatusOrdemServico.RECEBIDA));

        verify(templateProvider).render(eq("os-notificacao"), captor.capture());
        var vars = captor.getValue();
        assertThat(vars.get("nome")).isEqualTo("João Silva");
        assertThat(vars.get("osNumero")).isEqualTo(OS_ID);
        assertThat(vars.get("veiculo")).isEqualTo("Honda Civic");
    }

    @Test
    @DisplayName("deve incluir reclamação nas variáveis para status RECEBIDA")
    void deveIncluirReclamacaoParaStatusRecebida() {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

        useCase.execute(buildOs(StatusOrdemServico.RECEBIDA));

        verify(templateProvider).render(eq("os-notificacao"), captor.capture());
        assertThat(captor.getValue()).containsKey("reclamacao");
    }

    @Test
    @DisplayName("deve incluir reclamação nas variáveis para status EM_DIAGNOSTICO")
    void deveIncluirReclamacaoParaStatusEmDiagnostico() {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

        useCase.execute(buildOs(StatusOrdemServico.EM_DIAGNOSTICO));

        verify(templateProvider).render(anyString(), captor.capture());
        assertThat(captor.getValue()).containsKey("reclamacao");
    }

    @Test
    @DisplayName("deve incluir reclamação nas variáveis para status AGUARDANDO_APROVACAO")
    void deveIncluirReclamacaoParaStatusAguardandoAprovacao() {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

        useCase.execute(buildOs(StatusOrdemServico.AGUARDANDO_APROVACAO));

        verify(templateProvider).render(anyString(), captor.capture());
        assertThat(captor.getValue()).containsKey("reclamacao");
    }

    @Test
    @DisplayName("deve não incluir reclamação para status APROVADA")
    void deveNaoIncluirReclamacaoParaStatusAprovada() {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

        useCase.execute(buildOs(StatusOrdemServico.APROVADA));

        verify(templateProvider).render(anyString(), captor.capture());
        assertThat(captor.getValue()).doesNotContainKey("reclamacao");
    }

    @Test
    @DisplayName("deve incluir servicos e valorTotal para status AGUARDANDO_APROVACAO com serviços")
    void deveIncluirServicosParaAguardandoAprovacao() {
        var exec = buildExec(10L, StatusExecucaoServico.PENDENTE);
        var os = buildOs(StatusOrdemServico.AGUARDANDO_APROVACAO, new ArrayList<>(List.of(exec)));

        when(servicoRepository.getAllActiveById(any())).thenReturn(Set.of(new Servico(99L, "Troca de óleo", "desc")));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

        useCase.execute(os);

        verify(templateProvider).render(anyString(), captor.capture());
        var vars = captor.getValue();
        assertThat(vars).containsKey("servicos");
        assertThat(vars).containsKey("valorTotal");
    }

    @Test
    @DisplayName("deve incluir servicosAprovados e servicosRecusados para status APROVADA")
    void deveIncluirAprovadosRecusadosParaAprovada() {
        var execAprovado = buildExec(10L, StatusExecucaoServico.APROVADO);
        var execRecusado = buildExec(11L, StatusExecucaoServico.RECUSADO);
        var os = buildOs(StatusOrdemServico.APROVADA, new ArrayList<>(List.of(execAprovado, execRecusado)));

        when(servicoRepository.getActiveById(anyLong())).thenReturn(new Servico(99L, "Troca de óleo", "desc"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

        useCase.execute(os);

        verify(templateProvider).render(anyString(), captor.capture());
        var vars = captor.getValue();
        assertThat(vars).containsKey("servicosAprovados");
        assertThat(vars).containsKey("servicosRecusados");
        assertThat(vars).containsKey("valorAprovado");
        assertThat(vars).containsKey("valorRecusado");
    }

    @Test
    @DisplayName("deve incluir servicosAprovados para status EM_EXECUCAO")
    void deveIncluirAprovadosParaEmExecucao() {
        var exec = buildExec(10L, StatusExecucaoServico.APROVADO);
        var os = buildOs(StatusOrdemServico.EM_EXECUCAO, new ArrayList<>(List.of(exec)));

        when(servicoRepository.getActiveById(anyLong())).thenReturn(new Servico(99L, "Troca de óleo", "desc"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

        useCase.execute(os);

        verify(templateProvider).render(anyString(), captor.capture());
        assertThat(captor.getValue()).containsKey("servicosAprovados");
    }

    @Test
    @DisplayName("deve incluir servicosRecusados e valorRecusado para status REPROVADA")
    void deveIncluirTodosRecusadosParaReprovada() {
        var exec = buildExec(10L, StatusExecucaoServico.RECUSADO);
        var os = buildOs(StatusOrdemServico.REPROVADA, new ArrayList<>(List.of(exec)));

        when(servicoRepository.getActiveById(anyLong())).thenReturn(new Servico(99L, "Troca de óleo", "desc"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

        useCase.execute(os);

        verify(templateProvider).render(anyString(), captor.capture());
        var vars = captor.getValue();
        assertThat(vars).containsKey("servicosRecusados");
        assertThat(vars).containsKey("valorRecusado");
        assertThat(vars.get("reprovada")).isEqualTo(true);
    }

    @Test
    @DisplayName("deve calcular total do serviço somando mão de obra e peças")
    void deveCalcularTotalDoServico() {
        var peca = new PecaAlocada(1L, 50L, 10L, new BigDecimal("30.00"), 2, 2, 0, 0,
                StatusPecaAlocada.RESERVADA, LocalDateTime.now());
        var exec = new ExecucaoServico(10L, 99L, OS_ID, StatusExecucaoServico.PENDENTE,
                new BigDecimal("100.00"), new HashSet<>(Set.of(peca)), null, null, null, null, LocalDateTime.now());
        var os = buildOs(StatusOrdemServico.AGUARDANDO_APROVACAO, new ArrayList<>(List.of(exec)));

        var pecaDomain = new Peca(50L, "Filtro", "desc", new BigDecimal("35.00"));
        when(servicoRepository.getAllActiveById(any())).thenReturn(Set.of(new Servico(99L, "Troca de óleo", "desc")));
        when(pecaRepository.findAllByIds(any())).thenReturn(List.of(pecaDomain));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        useCase.execute(os);

        verify(templateProvider).render(anyString(), captor.capture());
        @SuppressWarnings("unchecked")
        var servicos = (List<Map<String, Object>>) captor.getValue().get("servicos");
        // total = mão de obra (100) + peca.valor (35) * qtd_solicitada (2) = 170
        assertThat((BigDecimal) servicos.get(0).get("valor")).isEqualByComparingTo("170.00");
    }

    @Test
    @DisplayName("deve construir timeline com 6 passos para status normal (não reprovada)")
    void deveConstruirTimelineParaStatusNormal() {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

        useCase.execute(buildOs(StatusOrdemServico.EM_DIAGNOSTICO));

        verify(templateProvider).render(anyString(), captor.capture());
        @SuppressWarnings("unchecked")
        var timeline = (List<Map<String, Object>>) captor.getValue().get("timeline");
        assertThat(timeline).hasSize(7);
    }

    @Test
    @DisplayName("deve construir timeline com 4 passos para status REPROVADA")
    void deveConstruirTimelineParaStatusReprovada() {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

        useCase.execute(buildOs(StatusOrdemServico.REPROVADA));

        verify(templateProvider).render(anyString(), captor.capture());
        @SuppressWarnings("unchecked")
        var timeline = (List<Map<String, Object>>) captor.getValue().get("timeline");
        assertThat(timeline).hasSize(4);
    }

    @Test
    @DisplayName("deve marcar passo atual como ATUAL para status EM_DIAGNOSTICO")
    void deveMarcaPassoAtualComoAtualParaEmDiagnostico() {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

        useCase.execute(buildOs(StatusOrdemServico.EM_DIAGNOSTICO));

        verify(templateProvider).render(anyString(), captor.capture());
        @SuppressWarnings("unchecked")
        var timeline = (List<Map<String, Object>>) captor.getValue().get("timeline");

        assertThat(timeline.get(0).get("status")).isEqualTo("CONCLUIDO"); // RECEBIDA
        assertThat(timeline.get(1).get("status")).isEqualTo("ATUAL");     // EM_DIAGNOSTICO
        assertThat(timeline.get(2).get("status")).isEqualTo("PENDENTE");  // AGUARDANDO_APROVACAO
    }

    @Test
    @DisplayName("deve marcar passo REPROVADA como RECUSADO na timeline")
    void deveMarcaPassoReprovadoComoRecusado() {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

        useCase.execute(buildOs(StatusOrdemServico.REPROVADA));

        verify(templateProvider).render(anyString(), captor.capture());
        @SuppressWarnings("unchecked")
        var timeline = (List<Map<String, Object>>) captor.getValue().get("timeline");

        assertThat(timeline.get(3).get("status")).isEqualTo("RECUSADO"); // REPROVADA step
    }

    @Test
    @DisplayName("deve marcar passo RECEBIDA como CONCLUIDO")
    void deveMarcaRecebidaComoConcluido() {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);

        useCase.execute(buildOs(StatusOrdemServico.RECEBIDA));

        verify(templateProvider).render(anyString(), captor.capture());
        @SuppressWarnings("unchecked")
        var timeline = (List<Map<String, Object>>) captor.getValue().get("timeline");

        assertThat(timeline.get(0).get("status")).isEqualTo("CONCLUIDO");
    }

    @Test
    @DisplayName("deve usar assunto correto de e-mail para cada status")
    void deveUsarAssuntoCorretoParaCadaStatus() {
        record CasoTeste(StatusOrdemServico status, String assuntoEsperado) {}
        var casos = List.of(
                new CasoTeste(StatusOrdemServico.RECEBIDA, "Ordem de Serviço Aberta"),
                new CasoTeste(StatusOrdemServico.EM_DIAGNOSTICO, "Diagnóstico Iniciado"),
                new CasoTeste(StatusOrdemServico.AGUARDANDO_APROVACAO, "Diagnóstico Finalizado"),
                new CasoTeste(StatusOrdemServico.APROVADA, "Serviços Aprovados"),
                new CasoTeste(StatusOrdemServico.EM_EXECUCAO, "Serviços em Execução"),
                new CasoTeste(StatusOrdemServico.FINALIZADA, "Serviços Finalizados"),
                new CasoTeste(StatusOrdemServico.REPROVADA, "Serviços Recusados"),
                new CasoTeste(StatusOrdemServico.ENTREGUE, "Veículo Entregue")
        );

        for (var caso : casos) {
            ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
            useCase.execute(buildOs(caso.status()));
            verify(emailProvider, atLeastOnce()).send(any(), subjectCaptor.capture(), any());
            assertThat(subjectCaptor.getValue())
                    .as("Assunto para status %s", caso.status())
                    .contains(caso.assuntoEsperado());
            clearInvocations(emailProvider);
        }
    }
}
