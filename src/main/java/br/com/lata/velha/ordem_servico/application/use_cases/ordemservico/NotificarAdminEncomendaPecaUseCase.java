package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.authentication.domain.repositories.UserRepository;
import br.com.lata.velha.ordem_servico.application.ports.EmailProvider;
import br.com.lata.velha.ordem_servico.application.ports.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificarAdminEncomendaPecaUseCase {

    private final FuncionarioRepository funcionarioRepository;
    private final UserRepository userRepository;
    private final PecaRepository pecaRepository;
    private final EmailProvider emailProvider;
    private final EmailTemplateProvider templateProvider;

    public record Input(Long osId, Long servicoId, Long pecaId, Integer quantidade, String servicoNome) {}

    public void execute(Input input) {
        Peca peca;
        try {
            peca = pecaRepository.getActiveById(input.pecaId());
        } catch (Exception e) {
            log.warn("Peça {} não encontrada ao tentar notificar encomenda", input.pecaId());
            return;
        }

        var admins = funcionarioRepository.findAllByCargoNome("ADMIN");
        if (admins.isEmpty()) {
            log.warn("Nenhum admin encontrado para notificar encomenda de peça");
            return;
        }

        admins.forEach(admin -> {
            try {
                var user = userRepository.getById(admin.getUserId());
                var html = templateProvider.render("peca-encomenda", buildVariables(admin.getNome(), input, peca));
                emailProvider.send(user.getEmail().getValor(), "Encomenda de Peça — OS Nº " + input.osId(), html);
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
