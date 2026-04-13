// application/usecase/ordemservico/NotificarOSCriadaUseCase.java

package br.com.lata.velha.application.usecase.ordemservico;

import br.com.lata.velha.application.port.EmailProvider;
import br.com.lata.velha.application.port.EmailTemplateProvider;
import br.com.lata.velha.domain.model.OrdemServico;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificarOSCriadaUseCase {

    private final EmailProvider emailProvider;
    private final EmailTemplateProvider templateProvider;

    public void execute(OrdemServico os, String nomeProprietario,
                        String emailProprietario, String veiculoDescricao) {
        try {
            Map<String, Object> variables = Map.of(
                    "nome", nomeProprietario,
                    "osNumero", os.getId(),
                    "veiculo", veiculoDescricao,
                    "reclamacao", os.getReclamacaoCliente(),
                    "status", os.getStatus().name()
            );

            String html = templateProvider.render("os-criada", variables);
            emailProvider.send(emailProprietario, "Ordem de Serviço Aberta - Lata Velha", html);

        } catch (Exception e) {
            log.error("Falha ao enviar email de OS para: {}", emailProprietario, e);
        }
    }
}