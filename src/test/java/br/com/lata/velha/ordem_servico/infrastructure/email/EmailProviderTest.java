package br.com.lata.velha.ordem_servico.infrastructure.email;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailProviderTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private GmailEmailProvider gmailEmailProvider;

    @InjectMocks
    private ThymeleafEmailTemplateProvider thymeleafEmailTemplateProvider;

    // ------------------------------------------------------------------ GmailEmailProvider

    @Test
    void gmailEmailProvider_deveEnviarEmailComSucesso() throws Exception {
        var mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        gmailEmailProvider.send("dest@email.com", "Assunto", "<p>Corpo HTML</p>");

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void gmailEmailProvider_deveLancarRuntimeExceptionQuandoMessagingException() throws Exception {
        var badMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(badMessage);
        doThrow(new RuntimeException("send error")).when(mailSender).send(any(MimeMessage.class));

        assertThatThrownBy(() -> gmailEmailProvider.send("dest@email.com", "Assunto", "<p>Corpo</p>"))
                .isInstanceOf(RuntimeException.class);
    }

    // ------------------------------------------------------------------ ThymeleafEmailTemplateProvider

    @Test
    void thymeleafEmailTemplateProvider_deveRenderizarTemplateComVariaveis() {
        var variables = Map.<String, Object>of("nome", "João", "total", "R$ 500,00");
        when(templateEngine.process(eq("notificacao-os"), any(org.thymeleaf.context.IContext.class))).thenReturn("<html>João</html>");

        var result = thymeleafEmailTemplateProvider.render("notificacao-os", variables);

        assertThat(result).isEqualTo("<html>João</html>");
        verify(templateEngine).process(eq("notificacao-os"), any(org.thymeleaf.context.IContext.class));
    }

    @Test
    void thymeleafEmailTemplateProvider_devePassarVariaveisParaContexto() {
        var variables = Map.<String, Object>of("chave", "valor");
        when(templateEngine.process(any(String.class), any(org.thymeleaf.context.IContext.class))).thenReturn("rendered");

        var result = thymeleafEmailTemplateProvider.render("template", variables);

        assertThat(result).isEqualTo("rendered");
    }

    @Test
    void thymeleafEmailTemplateProvider_deveRenderizarComMapaVazio() {
        when(templateEngine.process(eq("simples"), any(org.thymeleaf.context.IContext.class))).thenReturn("<p>sem vars</p>");

        var result = thymeleafEmailTemplateProvider.render("simples", Map.of());

        assertThat(result).isEqualTo("<p>sem vars</p>");
    }
}
