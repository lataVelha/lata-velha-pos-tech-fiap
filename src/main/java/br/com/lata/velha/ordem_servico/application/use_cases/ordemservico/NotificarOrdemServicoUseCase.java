package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.ports.EmailProvider;
import br.com.lata.velha.ordem_servico.application.ports.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
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
            Proprietario proprietario = proprietarioRepository.findActiveById(os.getProprietarioId());
            Veiculo veiculo = veiculoRepository.findActiveById(os.getVeiculoId());

            Map<String, Object> variables = new HashMap<>();
            variables.put("nome", proprietario.getNome());
            variables.put("osNumero", os.getId());
            variables.put("veiculo", veiculo.getMarca() + " " + veiculo.getModelo());
            variables.put("timeline", buildTimeline(os.getStatus()));

            StatusConfig config = getConfig(os.getStatus());
            variables.put("subtitulo", config.subtitulo);
            variables.put("mensagem", config.mensagem);

            addOptionalData(variables, os);

            String html = templateProvider.render("os-notificacao", variables);
            emailProvider.send(proprietario.getEmail(), config.assuntoEmail + " - Lata Velha", html);

            log.info("Email OS [{}] status [{}] enviado para: {}", os.getId(), os.getStatus(), proprietario.getEmail());
        } catch (Exception e) {
            log.error("Falha ao enviar email para OS: {} status: {}", os.getId(), os.getStatus(), e);
        }
    }

    // --- config por status ---

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
            case EM_EXECUCAO -> new StatusConfig(
                    "Serviços Aprovados",
                    "Os serviços foram aprovados e nossa equipe já está trabalhando no seu veículo.",
                    "Serviços Aprovados"
            );
            case FINALIZADA -> new StatusConfig(
                    "Serviços Finalizados",
                    "Todos os serviços do seu veículo foram concluídos. Seu veículo está pronto para retirada.",
                    "Serviços Finalizados"
            );
            case ENTREGUE -> new StatusConfig(
                    "Veículo Entregue",
                    "Seu veículo foi entregue. Agradecemos a confiança!",
                    "Veículo Entregue"
            );
        };
    }

    // --- dados opcionais por status ---

    private void addOptionalData(Map<String, Object> variables, OrdemServico os) {
        // Reclamação (aparece em RECEBIDA, EM_DIAGNOSTICO, AGUARDANDO_APROVACAO)
        if (os.getStatus() == StatusOrdemServico.RECEBIDA
                || os.getStatus() == StatusOrdemServico.EM_DIAGNOSTICO
                || os.getStatus() == StatusOrdemServico.AGUARDANDO_APROVACAO) {
            variables.put("reclamacao", os.getReclamacaoCliente());
        }

        // Serviços do diagnóstico (AGUARDANDO_APROVACAO)
        if (os.getStatus() == StatusOrdemServico.AGUARDANDO_APROVACAO
                && !os.getExecucaoServicos().isEmpty()) {
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

        // Serviços aprovados/recusados (EM_EXECUCAO)
        if (os.getStatus() == StatusOrdemServico.EM_EXECUCAO
                && !os.getExecucaoServicos().isEmpty()) {
            List<Map<String, Object>> aprovados = os.getExecucaoServicos().stream()
                    .filter(s -> s.getStatus() == StatusExecucaoServico.APROVADO)
                    .map(s -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("nome", s.getServico().getNome());
                        map.put("valor", s.calcularTotal());
                        return map;
                    })
                    .toList();

            List<Map<String, Object>> recusados = os.getExecucaoServicos().stream()
                    .filter(s -> s.getStatus() == StatusExecucaoServico.RECUSADO)
                    .map(s -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("nome", s.getServico().getNome());
                        map.put("valor", s.calcularTotal());
                        return map;
                    })
                    .toList();

            BigDecimal valorAprovado = os.getExecucaoServicos().stream()
                    .filter(s -> s.getStatus() == StatusExecucaoServico.APROVADO)
                    .map(s -> s.calcularTotal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal valorRecusado = os.getExecucaoServicos().stream()
                    .filter(s -> s.getStatus() == StatusExecucaoServico.RECUSADO)
                    .map(s -> s.calcularTotal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            variables.put("servicosAprovados", aprovados);
            variables.put("servicosRecusados", recusados);
            variables.put("valorAprovado", valorAprovado);
            variables.put("valorRecusado", valorRecusado);
        }
    }

    // --- timeline dinâmica ---

    private List<Map<String, Object>> buildTimeline(StatusOrdemServico statusAtual) {
        List<TimelineStep> steps = List.of(
                new TimelineStep(1, "Recebida", "Veículo recebido pela oficina", StatusOrdemServico.RECEBIDA),
                new TimelineStep(2, "Em Diagnóstico", "Mecânico avaliando o veículo", StatusOrdemServico.EM_DIAGNOSTICO),
                new TimelineStep(3, "Aguardando Aprovação", "Aprovação dos serviços identificados", StatusOrdemServico.AGUARDANDO_APROVACAO),
                new TimelineStep(4, "Em Execução", "Próximo passo: mecânico irá executar os serviços", StatusOrdemServico.EM_EXECUCAO),
                new TimelineStep(5, "Finalizada", "Serviços concluídos", StatusOrdemServico.FINALIZADA),
                new TimelineStep(6, "Entregue", "Veículo pronto para retirada", StatusOrdemServico.ENTREGUE)
        );

        int currentIndex = steps.stream()
                .filter(s -> s.status == statusAtual)
                .map(s -> s.numero)
                .findFirst()
                .orElse(1);

        List<Map<String, Object>> timeline = new ArrayList<>();
        for (TimelineStep step : steps) {
            Map<String, Object> map = new HashMap<>();
            map.put("numero", step.numero);
            map.put("titulo", step.titulo);
            map.put("descricao", step.descricao);

            if (step.numero < currentIndex) {
                map.put("status", "CONCLUIDO");
            } else if (step.numero == currentIndex) {
                map.put("status", "ATUAL");
            } else {
                map.put("status", "PENDENTE");
            }

            timeline.add(map);
        }

        return timeline;
    }

    // --- records auxiliares ---

    private record StatusConfig(String subtitulo, String mensagem, String assuntoEmail) {}

    private record TimelineStep(int numero, String titulo, String descricao, StatusOrdemServico status) {}
}