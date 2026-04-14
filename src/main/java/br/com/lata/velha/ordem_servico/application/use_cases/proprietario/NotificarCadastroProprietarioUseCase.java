package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.application.ports.EmailProvider;
import br.com.lata.velha.ordem_servico.application.ports.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificarCadastroProprietarioUseCase {

    private final EmailProvider emailProvider;
    private final EmailTemplateProvider templateProvider;

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