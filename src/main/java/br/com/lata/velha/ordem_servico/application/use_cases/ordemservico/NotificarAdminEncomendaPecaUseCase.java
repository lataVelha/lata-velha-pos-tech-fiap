package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.gateways.EmailProvider;
import br.com.lata.velha.ordem_servico.application.gateways.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class NotificarAdminEncomendaPecaUseCase {

    private static final Logger log = LoggerFactory.getLogger(NotificarAdminEncomendaPecaUseCase.class);

    private final NotificarAdminEncomendaPecaGateway gateway;
    private final EmailProvider emailProvider;
    private final EmailTemplateProvider templateProvider;

    public NotificarAdminEncomendaPecaUseCase(NotificarAdminEncomendaPecaGateway gateway,
                                              EmailProvider emailProvider,
                                              EmailTemplateProvider templateProvider) {
        this.gateway = gateway;
        this.emailProvider = emailProvider;
        this.templateProvider = templateProvider;
    }

    public record Input(Long osId, Long servicoId, Long pecaId, Integer quantidade, String servicoNome) {}

    public void execute(Input input) {
        Peca peca;
        try {
            var pecaOpt = gateway.findPecaPorId(input.pecaId());
            if (pecaOpt.isEmpty()) {
                log.warn("Peça {} não encontrada ao tentar notificar encomenda", input.pecaId());
                return;
            }
            peca = pecaOpt.get();
        } catch (Exception e) {
            log.warn("Peça {} não encontrada ao tentar notificar encomenda", input.pecaId());
            return;
        }

        var admins = gateway.getFuncionariosAdmin();
        if (admins.isEmpty()) {
            log.warn("Nenhum admin encontrado para notificar encomenda de peça");
            return;
        }

        admins.forEach(admin -> {
            try {
                var email = gateway.getEmailDoUsuario(admin.getUserId());
                var html = templateProvider.render("peca-encomenda", buildVariables(admin.getNome(), input, peca));
                emailProvider.send(email, "Encomenda de Peça — OS Nº " + input.osId(), html);
            } catch (Exception e) {
                log.error("Falha ao notificar admin {} sobre encomenda de peça", admin.getNome(), e);
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
