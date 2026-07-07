package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.application.gateways.EmailProvider;
import br.com.lata.velha.ordem_servico.application.gateways.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class NotificarCadastroProprietarioUseCase {

    private static final Logger log = LoggerFactory.getLogger(NotificarCadastroProprietarioUseCase.class);

    private final EmailProvider emailProvider;
    private final EmailTemplateProvider templateProvider;

    public NotificarCadastroProprietarioUseCase(EmailProvider emailProvider, EmailTemplateProvider templateProvider) {
        this.emailProvider = emailProvider;
        this.templateProvider = templateProvider;
    }

    public void execute(Proprietario proprietario) {
        try {
            Map<String, Object> variables = Map.of(
                    "nome", proprietario.getNome(),
                    "documento", proprietario.getDocumento().getMasked()
            );

            String html = templateProvider.render("proprietario-cadastro", variables);
            emailProvider.send(proprietario.getEmail(), "Bem-vindo ao Lata Velha!", html);

        } catch (Exception e) {
            log.error("Falha ao enviar email de cadastro para: {}", proprietario.getEmail(), e);
        }
    }
}
