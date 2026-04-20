package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.ports.EmailProvider;
import br.com.lata.velha.ordem_servico.application.ports.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificarOrdemServicoUseCase {

    private final EmailProvider emailProvider;
    private final EmailTemplateProvider templateProvider;
    private final ProprietarioRepository proprietarioRepository;
    private final VeiculoRepository veiculoRepository;

    public void execute(OrdemServico os) {
        try {
            Proprietario proprietario = proprietarioRepository.getActiveById(os.getProprietarioId());
            Veiculo veiculo = veiculoRepository.getActiveById(os.getVeiculoId());

            Map<String, Object> variables = buildVariables(os, proprietario, veiculo);
            String assunto = getAssunto(os.getStatus());

            String html = templateProvider.render("os-notificacao", variables);
            emailProvider.send(proprietario.getEmail(), assunto, html);
        } catch (Exception e) {
            log.error("Falha ao enviar email para OS: {} status: {}", os.getId(), os.getStatus(), e);
        }
    }

    private Map<String, Object> buildVariables(OrdemServico os, Proprietario proprietario, Veiculo veiculo) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nome", proprietario.getNome());
        variables.put("osNumero", os.getId());
        variables.put("veiculo", veiculo.getMarca() + " " + veiculo.getModelo());
        variables.put("timeline", buildTimeline(os.getStatus()));
        variables.put("reprovada", os.getStatus() == StatusOrdemServico.REPROVADA);

        StatusConfig config = getConfig(os.getStatus());
        variables.put("subtitulo", config.subtitulo());
        variables.put("mensagem", config.mensagem());

        addReclamacao(variables, os);
        addServicos(variables, os);
        addAprovadosRecusados(variables, os);
        addTodosRecusados(variables, os);

        return variables;
    }

    private String getAssunto(StatusOrdemServico status) {
        return getConfig(status).assuntoEmail() + " - Lata Velha";
    }

    private StatusConfig getConfig(StatusOrdemServico status) {
        return switch (status) {
            case RECEBIDA -> new StatusConfig(
                    "Ordem de Serviço",
                    "Sua Ordem de Serviço foi aberta com sucesso. Acompanhe abaixo o progresso.",
                    "Ordem de Serviço Aberta"
            );
            case EM_DIAGNOSTICO -> new StatusConfig(
                    "Diagnóstico Iniciado",
                    "Nosso mecânico iniciou o diagnóstico do seu veículo.",
                    "Diagnóstico Iniciado"
            );
            case AGUARDANDO_APROVACAO -> new StatusConfig(
                    "Diagnóstico Finalizado",
                    "O diagnóstico do seu veículo foi finalizado. Confira abaixo os serviços identificados.",
                    "Diagnóstico Finalizado"
            );
            case APROVADA -> new StatusConfig(
                    "Serviços Aprovados",
                    "Os serviços foram aprovados. Em breve nossa equipe iniciará a execução.",
                    "Serviços Aprovados"
            );
            case EM_EXECUCAO -> new StatusConfig(
                    "Serviços em Execução",
                    "Nossa equipe já está trabalhando no seu veículo.",
                    "Serviços em Execução"
            );
            case FINALIZADA -> new StatusConfig(
                    "Serviços Finalizados",
                    "Todos os serviços do seu veículo foram concluídos. Seu veículo está pronto para retirada.",
                    "Serviços Finalizados"
            );
            case REPROVADA -> new StatusConfig(
                    "Serviços Recusados",
                    "Todos os serviços foram reprovados!",
                    "Serviços Recusados"
            );
            case ENTREGUE -> new StatusConfig(
                    "Veículo Entregue",
                    "Seu veículo foi entregue. Agradecemos a confiança!",
                    "Veículo Entregue"
            );
        };
    }

    private void addReclamacao(Map<String, Object> variables, OrdemServico os) {
        if (os.getStatus() == StatusOrdemServico.RECEBIDA
                || os.getStatus() == StatusOrdemServico.EM_DIAGNOSTICO
                || os.getStatus() == StatusOrdemServico.AGUARDANDO_APROVACAO) {
            variables.put("reclamacao", os.getReclamacaoCliente());
        }
    }

    private void addServicos(Map<String, Object> variables, OrdemServico os) {
        if (os.getStatus() != StatusOrdemServico.AGUARDANDO_APROVACAO
                || os.getExecucaoServicos().isEmpty()) {
            return;
        }

        List<Map<String, Object>> servicos = os.getExecucaoServicos().stream()
                .map(s -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("nome", s.getServico().getNome());
                    map.put("descricao", s.getServico().getDescricao());
                    map.put("valor", s.calcularTotal());
                    return map;
                })
                .toList();

        variables.put("servicos", servicos);
        variables.put("valorTotal", os.calcularValorTotal());
    }

    private void addAprovadosRecusados(Map<String, Object> variables, OrdemServico os) {
        if ((os.getStatus() != StatusOrdemServico.EM_EXECUCAO && os.getStatus() != StatusOrdemServico.APROVADA)
                || os.getExecucaoServicos().isEmpty()) {
            return;
        }

        List<Map<String, Object>> aprovados = os.getExecucaoServicos().stream()
                .filter(s -> s.getStatus() == StatusExecucaoServico.APROVADO)
                .map(this::toServicoMap)
                .toList();

        List<Map<String, Object>> recusados = os.getExecucaoServicos().stream()
                .filter(s -> s.getStatus() == StatusExecucaoServico.RECUSADO)
                .map(this::toServicoMap)
                .toList();

        variables.put("servicosAprovados", aprovados);
        variables.put("servicosRecusados", recusados);
        variables.put("valorAprovado", calcularTotal(os, StatusExecucaoServico.APROVADO));
        variables.put("valorRecusado", calcularTotal(os, StatusExecucaoServico.RECUSADO));
    }

    private void addTodosRecusados(Map<String, Object> variables, OrdemServico os) {
        if (os.getStatus() != StatusOrdemServico.REPROVADA
                || os.getExecucaoServicos().isEmpty()) {
            return;
        }

        List<Map<String, Object>> recusados = os.getExecucaoServicos().stream()
                .map(this::toServicoMap)
                .toList();

        BigDecimal valorRecusado = os.getExecucaoServicos().stream()
                .map(ExecucaoServico::calcularTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        variables.put("servicosRecusados", recusados);
        variables.put("valorRecusado", valorRecusado);
    }

    private Map<String, Object> toServicoMap(ExecucaoServico s) {
        Map<String, Object> map = new HashMap<>();
        map.put("nome", s.getServico().getNome());
        map.put("valor", s.calcularTotal());
        return map;
    }

    private BigDecimal calcularTotal(OrdemServico os, StatusExecucaoServico status) {
        return os.getExecucaoServicos().stream()
                .filter(s -> s.getStatus() == status)
                .map(ExecucaoServico::calcularTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<Map<String, Object>> buildTimeline(StatusOrdemServico statusAtual) {
        boolean isReprovada = statusAtual == StatusOrdemServico.REPROVADA;

        List<TimelineStep> steps;
        if (isReprovada) {
            steps = List.of(
                    new TimelineStep(1, "Recebida", "Veículo recebido pela oficina", StatusOrdemServico.RECEBIDA),
                    new TimelineStep(2, "Em Diagnóstico", "Mecânico avaliou o veículo", StatusOrdemServico.EM_DIAGNOSTICO),
                    new TimelineStep(3, "Aguardando Aprovação", "Serviços apresentados ao cliente", StatusOrdemServico.AGUARDANDO_APROVACAO),
                    new TimelineStep(4, "Reprovada", "Todos os serviços foram recusados", StatusOrdemServico.REPROVADA)
            );
        } else {
            steps = List.of(
                    new TimelineStep(1, "Recebida", "Veículo recebido pela oficina", StatusOrdemServico.RECEBIDA),
                    new TimelineStep(2, "Em Diagnóstico", "Mecânico avaliando o veículo", StatusOrdemServico.EM_DIAGNOSTICO),
                    new TimelineStep(3, "Aguardando Aprovação", "Aprovação dos serviços identificados", StatusOrdemServico.AGUARDANDO_APROVACAO),
                    new TimelineStep(4, "Aprovada", "Serviços aprovados, aguardando início da execução", StatusOrdemServico.APROVADA),
                    new TimelineStep(5, "Em Execução", "Mecânico executando os serviços", StatusOrdemServico.EM_EXECUCAO),
                    new TimelineStep(6, "Finalizada", "Serviços concluídos", StatusOrdemServico.FINALIZADA),
                    new TimelineStep(7, "Entregue", "Veículo retirado pelo cliente", StatusOrdemServico.ENTREGUE)
            );
        }

        int currentIndex = steps.stream()
                .filter(s -> s.status() == statusAtual)
                .map(TimelineStep::numero)
                .findFirst()
                .orElse(1);

        boolean isStepConcluido = statusAtual == StatusOrdemServico.RECEBIDA
                || statusAtual == StatusOrdemServico.FINALIZADA
                || statusAtual == StatusOrdemServico.ENTREGUE
                || statusAtual == StatusOrdemServico.REPROVADA
                || statusAtual == StatusOrdemServico.APROVADA;

        List<Map<String, Object>> timeline = new ArrayList<>();
        for (TimelineStep step : steps) {
            Map<String, Object> map = new HashMap<>();
            map.put("numero", step.numero());
            map.put("titulo", step.titulo());
            map.put("descricao", step.descricao());

            if (step.numero() < currentIndex) {
                map.put("status", "CONCLUIDO");
            } else if (step.numero() == currentIndex) {
                if (isStepConcluido) {
                    map.put("status", isReprovada && step.status() == StatusOrdemServico.REPROVADA ? "RECUSADO" : "CONCLUIDO");
                } else {
                    map.put("status", "ATUAL");
                }
            } else {
                map.put("status", "PENDENTE");
            }

            timeline.add(map);
        }

        return timeline;
    }

    private record StatusConfig(String subtitulo, String mensagem, String assuntoEmail) {}

    private record TimelineStep(int numero, String titulo, String descricao, StatusOrdemServico status) {}
}
