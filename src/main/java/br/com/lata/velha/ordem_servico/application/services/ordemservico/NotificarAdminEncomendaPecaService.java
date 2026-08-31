package br.com.lata.velha.ordem_servico.application.services.ordemservico;

import br.com.lata.velha.ordem_servico.application.gateways.EmailProvider;
import br.com.lata.velha.ordem_servico.application.gateways.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.shared.application.logging.Logger;

import java.util.HashMap;
import java.util.Map;

public class NotificarAdminEncomendaPecaService {

    private final NotificarAdminEncomendaPecaGateway gateway;
    private final EmailProvider emailProvider;
    private final EmailTemplateProvider templateProvider;
    private final Logger logger;

    public NotificarAdminEncomendaPecaService(NotificarAdminEncomendaPecaGateway gateway,
                                              EmailProvider emailProvider,
                                              EmailTemplateProvider templateProvider,
                                              Logger logger) {
        this.gateway = gateway;
        this.emailProvider = emailProvider;
        this.templateProvider = templateProvider;
        this.logger = logger;
    }

    public record Input(Long osId, Long servicoId, Long pecaId, Integer quantidade, String servicoNome) {}

    public void execute(Input input) {
        logger.logInfo("Notificando admins sobre encomenda de peça - osId={}, pecaId={}, quantidade={}",
                input.osId(), input.pecaId(), input.quantidade());

        Peca peca;
        try {
            var pecaOpt = gateway.findPecaPorId(input.pecaId());
            if (pecaOpt.isEmpty()) {
                logger.logWarn("Peça não encontrada ao tentar notificar encomenda - pecaId={}", input.pecaId());
                return;
            }
            peca = pecaOpt.get();
        } catch (Exception e) {
            logger.logError("Falha ao verificar peça para notificação de encomenda - pecaId=" + input.pecaId(), e);
            return;
        }

        var admins = gateway.getFuncionariosAdmin();
        if (admins.isEmpty()) {
            logger.logWarn("Nenhum admin encontrado para notificar encomenda de peça - osId={}", input.osId());
            return;
        }

        admins.forEach(admin -> {
            try {
                var email = gateway.getEmailDoUsuario(admin.getUserId());
                var html = templateProvider.render("peca-encomenda", buildVariables(admin.getNome(), input, peca));
                emailProvider.send(email, "Encomenda de Peça — OS Nº " + input.osId(), html);
            } catch (Exception e) {
                logger.logError("Falha ao notificar admin sobre encomenda de peça - osId=" + input.osId() + ", adminUserId=" + admin.getUserId(), e);
            }
        });
    }

    private Map<String, Object> buildVariables(String nomeAdmin, Input input, Peca peca) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("nome", nomeAdmin);
        vars.put("osNumero", input.osId());
        vars.put("servicoNome", input.servicoNome());
        vars.put("pecaNome", peca.getNome());
        vars.put("quantidade", input.quantidade());
        return vars;
    }
}
